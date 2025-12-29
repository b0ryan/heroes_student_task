package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        if (unitList == null || unitList.isEmpty()) {
            return new Army(new ArrayList<>());
        }

        List<Unit> selectedUnits = new ArrayList<>();
        
        List<UnitEfficiency> efficiencies = new ArrayList<>();
        
        for (Unit unit : unitList) {
            if (unit == null || unit.getCost() <= 0) {
                continue;
            }
            
            double attackEfficiency = (double) unit.getBaseAttack() / unit.getCost();
            double healthEfficiency = (double) unit.getHealth() / unit.getCost();
            double totalEfficiency = attackEfficiency * 0.6 + healthEfficiency * 0.4;
            efficiencies.add(new UnitEfficiency(unit, totalEfficiency, attackEfficiency, healthEfficiency));
        }
        
        efficiencies.sort((a, b) -> Double.compare(b.totalEfficiency, a.totalEfficiency));
        
        int totalPoints = 0;
        Map<Unit, Integer> unitCounts = new HashMap<>();
        
        for (UnitEfficiency eff : efficiencies) {
            Unit unit = eff.unit;
            if (unit == null || unit.getCost() <= 0) {
                continue;
            }
            
            int maxCount = 11; 
            int currentCount = unitCounts.getOrDefault(unit, 0);
            
            while (currentCount < maxCount && totalPoints + unit.getCost() <= maxPoints) {
                selectedUnits.add(unit);
                totalPoints += unit.getCost();
                currentCount++;
                unitCounts.put(unit, currentCount);
            }
        }
        
        return new Army(selectedUnits);
    }
    
    private static class UnitEfficiency {
        Unit unit;
        double totalEfficiency;
        double attackEfficiency;
        double healthEfficiency;
        
        UnitEfficiency(Unit unit, double totalEfficiency, double attackEfficiency, double healthEfficiency) {
            this.unit = unit;
            this.totalEfficiency = totalEfficiency;
            this.attackEfficiency = attackEfficiency;
            this.healthEfficiency = healthEfficiency;
        }
    }
}