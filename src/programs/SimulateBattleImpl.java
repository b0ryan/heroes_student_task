package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        while (true) {
            List<Unit> allUnits = new ArrayList<>();
            allUnits.addAll(getAliveUnits(playerArmy));
            allUnits.addAll(getAliveUnits(computerArmy));
            
            if (allUnits.isEmpty()) {
                break;
            }
            
            allUnits.sort((u1, u2) -> Integer.compare(u2.getBaseAttack(), u1.getBaseAttack()));
            
            boolean hasMoved = false;
            
            for (Unit unit : allUnits) {
                if (!unit.isAlive()) {
                    continue;
                }
                
                boolean isPlayerUnit = playerArmy.getUnits().contains(unit);
                Army enemyArmy = isPlayerUnit ? computerArmy : playerArmy;
                

                Unit target = unit.getProgram().attack();
                
                if (target != null) {
                    printBattleLog.printBattleLog(unit, target);
                    hasMoved = true;
                }
                
                if (!hasAliveUnits(playerArmy) && !hasAliveUnits(computerArmy)) {
                    break;
                }
            }
            

            if (!hasMoved) {
                break;
            }
        }
    }
    
    private boolean hasAliveUnits(Army army) {
        return army.getUnits().stream().anyMatch(Unit::isAlive);
    }
    
    private List<Unit> getAliveUnits(Army army) {
        return army.getUnits().stream()
                .filter(Unit::isAlive)
                .collect(Collectors.toList());
    }
}