package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;
    private static final int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
    
    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        List<Edge> path = new ArrayList<>();
        
        if (attackUnit == null || targetUnit == null) {
            return path;
        }
        
        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int targetX = targetUnit.getxCoordinate();
        int targetY = targetUnit.getyCoordinate();
        if (startX == targetX && startY == targetY) {
            path.add(new Edge(startX, startY));
            return path;
        }
        
        Set<String> obstacles = new HashSet<>();
        if (existingUnitList != null) {
            for (Unit unit : existingUnitList) {
                if (unit != null && unit.isAlive()) {
                    if (!(unit.getxCoordinate() == startX && unit.getyCoordinate() == startY) &&
                        !(unit.getxCoordinate() == targetX && unit.getyCoordinate() == targetY)) {
                        obstacles.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
                    }
                }
            }
        }
        
        Queue<Cell> queue = new LinkedList<>();
        Map<String, Cell> visited = new HashMap<>();
        Cell startCell = new Cell(startX, startY, null);
        queue.offer(startCell);
        visited.put(startX + "," + startY, startCell);
        
        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            
            if (current.x == targetX && current.y == targetY) {
                Cell cell = current;
                while (cell != null) {
                    path.add(0, new Edge(cell.x, cell.y));
                    cell = cell.parent;
                }
                return path;
            }
            
            for (int i = 0; i < dx.length; i++) {
                int newX = current.x + dx[i];
                int newY = current.y + dy[i];
                
                if (newX < 0 || newX >= WIDTH || newY < 0 || newY >= HEIGHT) {
                    continue;
                }
                
                String key = newX + "," + newY;
                if (obstacles.contains(key)) {
                    continue;
                }
                
                if (!visited.containsKey(key)) {
                    Cell newCell = new Cell(newX, newY, current);
                    queue.offer(newCell);
                    visited.put(key, newCell);
                }
            }
        }
        
        return path;
    }
    

    private static class Cell {
        int x;
        int y;
        Cell parent;
        
        Cell(int x, int y, Cell parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }
    }
}
