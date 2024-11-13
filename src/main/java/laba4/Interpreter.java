package laba4;

import laba3.Command;
import laba3.PolizEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class Interpreter {
    private final List<PolizEntry<Object>> polizEntries;
    private final Map<String, Integer> variables = new HashMap<>();
    private final Stack<Integer> stack = new Stack<>();
    private final Scanner scanner = new Scanner(System.in);

    public Interpreter(List<PolizEntry<Object>> polizEntries) {
        this.polizEntries = polizEntries;
    }

    public void execute() {
        int i = 0;
        while (i < polizEntries.size()) {
            PolizEntry<Object> entry = polizEntries.get(i);
            switch (entry.type()) {
                case CONSTANT -> stack.push((Integer) entry.value());
                case VARIABLE -> handleVariable((String) entry.value());
                case COMMAND -> i = handleCommand((Command) entry.value(), i);
                case JUMP_ADDRESS -> stack.push((Integer) entry.value());
            }
            i++;
        }
    }

    private void handleVariable(String varName) {
        if (!variables.containsKey(varName)) {
            System.out.print("Введите значение для переменной " + varName + ": ");
            int value = scanner.nextInt();
            variables.put(varName, value);
        }
        stack.push(variables.get(varName));
    }

    private int handleCommand(Command command, int currentIndex) {
        switch (command) {
            case ADD -> stack.push(stack.pop() + stack.pop());
            case SUB -> stack.push(-stack.pop() + stack.pop());
            case MUL -> stack.push(stack.pop() * stack.pop());
            case DIV -> {
                int divisor = stack.pop();
                int dividend = stack.pop();
                stack.push(dividend / divisor);
            }
            case SET -> {
                int value = stack.pop();
                String varName = (String) polizEntries.get(++currentIndex).value();
                variables.put(varName, value);
            }
            case OUTPUT -> System.out.println(stack.pop().toString());
            case CMPG -> stack.push(stack.pop() < stack.pop() ? 1 : 0);
            case CMPL -> stack.push(stack.pop() > stack.pop() ? 1 : 0);
            case CMPE -> stack.push(stack.pop().equals(stack.pop()) ? 1 : 0);
            case CMPNE -> stack.push(!stack.pop().equals(stack.pop()) ? 1 : 0);
            case CMPGE -> stack.push(stack.pop() <= stack.pop() ? 1 : 0);
            case CMPLE -> stack.push(stack.pop() >= stack.pop() ? 1 : 0);
            case NOT -> stack.push(stack.pop() == 0 ? 1 : 0);
            case AND -> stack.push(stack.pop() & stack.pop());
            case OR -> stack.push(stack.pop() | stack.pop());
            case JMP -> currentIndex = stack.pop() - 1;
            case JZ -> {
                int address = stack.pop();
                if (stack.pop() == 0) currentIndex = address - 1;
            }
            default -> throw new UnsupportedOperationException("Неизвестная команда: " + command);
        }
        return currentIndex;
    }
}
