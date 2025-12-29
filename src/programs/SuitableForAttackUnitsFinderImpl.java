package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();
        
        if (unitsByRow == null || unitsByRow.isEmpty()) {
            return suitableUnits;
        }
        
        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) {
                continue;
            }
            
            Set<Integer> occupiedY = new HashSet<>();
            for (Unit unit : row) {
                if (unit != null && unit.isAlive()) {
                    occupiedY.add(unit.getyCoordinate());
                }
            }
            
            for (Unit unit : row) {
                if (unit == null || !unit.isAlive()) {
                    continue;
                }
                
                int unitY = unit.getyCoordinate();
                boolean isBlocked = false;
                
                if (isLeftArmyTarget) {
                    if (occupiedY.contains(unitY + 1)) {
                        isBlocked = true;
                    }
                } else {
                    if (occupiedY.contains(unitY - 1)) {
                        isBlocked = true;
                    }
                }
                
                if (!isBlocked) {
                    suitableUnits.add(unit);
                }
            }
        }
        
        return suitableUnits;
    }
}
