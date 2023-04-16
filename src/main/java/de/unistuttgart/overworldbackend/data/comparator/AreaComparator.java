package de.unistuttgart.overworldbackend.data.comparator;

import de.unistuttgart.overworldbackend.data.Area;
import de.unistuttgart.overworldbackend.data.Dungeon;

import java.util.Comparator;

public class AreaComparator implements Comparator<Area> {

    @Override
    public int compare(final Area a1, final Area a2) { //NOSONAR
        if (a1 instanceof Dungeon d1) {
            if (a2 instanceof Dungeon d2) {
                if (d1.getWorld().getIndex() == d2.getWorld().getIndex()) {
                    return Integer.compare(d1.getIndex(), d2.getIndex());
                } else {
                    return Integer.compare(d1.getWorld().getIndex(), d2.getWorld().getIndex());
                }
            } else {
                if (d1.getWorld().getIndex() == a2.getIndex()) {
                    return 1;
                } else {
                    return Integer.compare(d1.getWorld().getIndex(), a2.getIndex());
                }
            }
        } else {
            if (a2 instanceof Dungeon d2) {
                if (a1.getIndex() == d2.getWorld().getIndex()) {
                    return -1;
                } else {
                    return Integer.compare(a1.getIndex(), d2.getWorld().getIndex());
                }
            } else {
                return Integer.compare(a1.getIndex(), a2.getIndex());
            }
        }
    }
}
