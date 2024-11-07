package laba3;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PolizGenerator {
    private final List<PolizEntry<Object>> polizEntries = new ArrayList<>();

    public int addCommand(Command cmd) {
        polizEntries.add(new PolizEntry<>(EntryType.COMMAND, cmd));
        return polizEntries.size() - 1;
    }

    public int addVariable(String var) {
        polizEntries.add(new PolizEntry<>(EntryType.VARIABLE, var));
        return polizEntries.size() - 1;
    }

    public int addConstant(int val) {
        polizEntries.add(new PolizEntry<>(EntryType.CONSTANT, val));
        return polizEntries.size() - 1;
    }

    public int addJumpAddress(int address) {
        polizEntries.add(new PolizEntry<>(EntryType.JUMP_ADDRESS, address));
        return polizEntries.size() - 1;
    }

    public void setJumpAddress(int position, int address) {
        PolizEntry<Object> entry = polizEntries.get(position);
        polizEntries.set(position, new PolizEntry<>(entry.type(), address));
    }
}
