package com.code.codeR.service;

import com.code.codeR.model.CodingProblem;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class MainMethodGenerator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateMainClass(CodingProblem problem) {
        String methodName = problem.getMethodName();
        String returnType = problem.getReturnType();
        String paramsJson = problem.getParameters();

        StringBuilder sb = new StringBuilder();
        sb.append("import java.util.*;\n\n");
        sb.append("public class Main {\n");
        sb.append("    public static void main(String[] args) {\n");
        sb.append("        Scanner sc = new Scanner(System.in);\n");
        sb.append("        Solution solution = new Solution();\n\n");
        sb.append("        while (sc.hasNextLine()) {\n");
        sb.append("            String line = sc.nextLine();\n");
        sb.append("            if (line.trim().isEmpty()) continue;\n\n");
        sb.append("            try {\n");
        
        try {
            List<Map<String, String>> params = objectMapper.readValue(paramsJson, new TypeReference<>() {});
            int paramCount = params.size();
            
            sb.append("                String[] parts = splitArguments(line, ").append(paramCount).append(");\n");
            sb.append("                if (parts.length < ").append(paramCount).append(") continue;\n\n");
            
            int argIndex = 0;
            StringBuilder methodCallArgs = new StringBuilder();
            
            for (Map<String, String> param : params) {
                String type = param.get("type");
                String name = param.get("name");
                
                sb.append("                ").append(type).append(" ").append(name).append(" = ");
                
                if (type.equals("int")) {
                    sb.append("Integer.parseInt(parts[").append(argIndex).append("].trim());\n");
                } else if (type.equals("String")) {
                    sb.append("parseString(parts[").append(argIndex).append("].trim());\n");
                } else if (type.equals("int[]")) {
                    sb.append("parseIntArray(parts[").append(argIndex).append("].trim());\n");
                } else if (type.equals("double")) {
                    sb.append("Double.parseDouble(parts[").append(argIndex).append("].trim());\n");
                } else if (type.equals("long")) {
                    sb.append("Long.parseLong(parts[").append(argIndex).append("].trim());\n");
                } else if (type.equals("double[]")) {
                    sb.append("parseDoubleArray(parts[").append(argIndex).append("].trim());\n");
                } else if (type.equals("String[]")) {
                    sb.append("parseStringArray(parts[").append(argIndex).append("].trim());\n");
                } else if (type.equals("char")) {
                    sb.append("parts[").append(argIndex).append("].trim().replaceAll(\"^\\\"|\\\"$\", \"\").charAt(0);\n");
                } else if (type.equals("char[]")) {
                    sb.append("parseCharArray(parts[").append(argIndex).append("].trim());\n");
                } else if (type.equals("boolean")) {
                    sb.append("Boolean.parseBoolean(parts[").append(argIndex).append("].trim());\n");
                } else if (type.equals("ListNode")) {
                    sb.append("buildLinkedList(parts[").append(argIndex).append("].trim(), \"").append(problem.getSubtype()).append("\");\n");
                } else {
                    sb.append("null; // Unsupported type: ").append(type).append("\n");
                }
                
                if (argIndex > 0) methodCallArgs.append(", ");
                methodCallArgs.append(name);
                argIndex++;
            }
            
                if (returnType.equals("void")) {
                    sb.append("                solution.").append(methodName).append("(").append(methodCallArgs).append(");\n");
                } else {
                    sb.append("                ").append(returnType).append(" result = solution.").append(methodName).append("(").append(methodCallArgs).append(");\n");
                    sb.append("                printResult(result);\n");
                }
                
                sb.append("                System.out.println(\"---CASE_END---\");\n");
                sb.append("                System.out.flush();\n");

            } catch (JsonProcessingException e) {
                sb.append("                System.out.println(\"Error parsing problem parameters: ").append(e.getMessage()).append("\");\n");
                sb.append("                System.out.flush();\n");
            }
        
        sb.append("            } catch (Exception e) {\n");
        sb.append("                System.out.println(\"RUNTIME_ERROR: \" + e.getMessage());\n");
        sb.append("                System.out.println(\"---CASE_END---\");\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("    }\n\n");

        addHelperMethods(sb);

        sb.append("}\n");
        addListNodeClass(sb);
        return sb.toString();
    }

    private void addHelperMethods(StringBuilder sb) {
        // splitArguments helper (Smart split that respects [] and "")
        sb.append("    private static String[] splitArguments(String input, int count) {\n");
        sb.append("        input = input.trim();\n");
        sb.append("        if (count == 1) return new String[] { input };\n");
        sb.append("        \n");
        sb.append("        List<String> args = new ArrayList<>();\n");
        sb.append("        StringBuilder current = new StringBuilder();\n");
        sb.append("        boolean inBrackets = false;\n");
        sb.append("        boolean inQuotes = false;\n");
        sb.append("        \n");
        sb.append("        for (int i = 0; i < input.length(); i++) {\n");
        sb.append("            char ch = input.charAt(i);\n");
        sb.append("            if (ch == '[' && !inQuotes) inBrackets = true;\n");
        sb.append("            else if (ch == ']' && !inQuotes) inBrackets = false;\n");
        sb.append("            else if (ch == '\"' && !inBrackets) inQuotes = !inQuotes;\n");
        sb.append("            \n");
        sb.append("            if (Character.isWhitespace(ch) && !inBrackets && !inQuotes) {\n");
        sb.append("                if (current.length() > 0) {\n");
        sb.append("                    args.add(current.toString());\n");
        sb.append("                    current.setLength(0);\n");
        sb.append("                    // If we have enough arguments, stop splitting and take the rest as the last one\n");
        sb.append("                    if (args.size() == count - 1 && i < input.length() - 1) {\n");
        sb.append("                        args.add(input.substring(i + 1).trim());\n");
        sb.append("                        return args.toArray(new String[0]);\n");
        sb.append("                    }\n");
        sb.append("                }\n");
        sb.append("            } else {\n");
        sb.append("                current.append(ch);\n");
        sb.append("            }\n");
        sb.append("        }\n");
        sb.append("        if (current.length() > 0) args.add(current.toString());\n");
        sb.append("        return args.toArray(new String[0]);\n");
        sb.append("    }\n\n");

        // parseIntArray helper
        sb.append("    private static int[] parseIntArray(String input) {\n");
        sb.append("        if (input == null || input.trim().isEmpty() || input.trim().equals(\"[]\")) {\n");
        sb.append("            return new int[0];\n");
        sb.append("        }\n");
        sb.append("        input = input.trim();\n");
        sb.append("        if (input.startsWith(\"[\") && input.endsWith(\"]\")) {\n");
        sb.append("            input = input.substring(1, input.length() - 1);\n");
        sb.append("        }\n");
        sb.append("        String[] parts = input.split(\"[,\\\\s]+\");\n");
        sb.append("        List<Integer> list = new ArrayList<>();\n");
        sb.append("        for (String p : parts) {\n");
        sb.append("            if (!p.trim().isEmpty()) list.add(Integer.parseInt(p.trim()));\n");
        sb.append("        }\n");
        sb.append("        int[] res = new int[list.size()];\n");
        sb.append("        for(int i=0; i<list.size(); i++) res[i] = list.get(i);\n");
        sb.append("        return res;\n");
        sb.append("    }\n\n");

        sb.append("    private static double[] parseDoubleArray(String input) {\n");
        sb.append("        if (input == null || input.trim().isEmpty() || input.trim().equals(\"[]\")) return new double[0];\n");
        sb.append("        input = input.trim();\n");
        sb.append("        if (input.startsWith(\"[\") && input.endsWith(\"]\")) input = input.substring(1, input.length() - 1);\n");
        sb.append("        String[] parts = input.split(\"[,\\\\s]+\");\n");
        sb.append("        List<Double> list = new ArrayList<>();\n");
        sb.append("        for (String p : parts) if (!p.trim().isEmpty()) list.add(Double.parseDouble(p.trim()));\n");
        sb.append("        double[] res = new double[list.size()];\n");
        sb.append("        for(int i=0; i<list.size(); i++) res[i] = list.get(i);\n");
        sb.append("        return res;\n");
        sb.append("    }\n\n");

        sb.append("    private static String[] parseStringArray(String input) {\n        if (input == null || input.trim().isEmpty() || input.trim().equals(\"[]\")) return new String[0];\n        input = input.trim();\n        if (input.startsWith(\"[\") && input.endsWith(\"]\")) input = input.substring(1, input.length() - 1);\n        String[] parts = input.split(\"[,\\\\s]+\");\n        List<String> list = new ArrayList<>();\n        for (String p : parts) if (!p.trim().isEmpty()) list.add(p.trim().replaceAll(\"^\\\"|\\\"$\", \"\"));\n        return list.toArray(new String[0]);\n    }\n\n    private static char[] parseCharArray(String input) {\n        if (input == null || input.trim().isEmpty() || input.trim().equals(\"[]\")) return new char[0];\n        input = input.trim();\n        if (input.startsWith(\"[\") && input.endsWith(\"]\")) input = input.substring(1, input.length() - 1);\n        String[] parts = input.split(\"[,\\\\s]+\");\n        StringBuilder sb = new StringBuilder();\n        for (String p : parts) {\n            String s = p.trim().replaceAll(\"^\\\"|\\\"$\", \"\");\n            if (!s.isEmpty()) sb.append(s.charAt(0));\n        }\n        return sb.toString().toCharArray();\n    }\n\n");

        // parseString helper
        sb.append("    private static String parseString(String input) {\n");
        sb.append("        if (input == null) return \"\";\n");
        sb.append("        input = input.trim();\n");
        sb.append("        if (input.startsWith(\"\\\"\") && input.endsWith(\"\\\"\")) {\n");
        sb.append("            return input.substring(1, input.length() - 1);\n");
        sb.append("        }\n");
        sb.append("        return input;\n");
        sb.append("    }\n\n");

        // printResult helper
        sb.append("    private static void printResult(Object result) {\n");
        sb.append("        if (result == null) {\n");
        sb.append("            System.out.println(\"null\");\n");
        sb.append("        } else if (result instanceof int[]) {\n");
        sb.append("            System.out.println(Arrays.toString((int[]) result));\n");
        sb.append("        } else if (result instanceof double[]) {\n");
        sb.append("            System.out.println(Arrays.toString((double[]) result));\n");
        sb.append("        } else if (result instanceof Object[]) {\n");
        sb.append("            System.out.println(Arrays.deepToString((Object[]) result));\n");
        sb.append("        } else if (result instanceof List) {\n");
        sb.append("            System.out.println(result.toString());\n");
        sb.append("        } else if (result.getClass().getSimpleName().equals(\"ListNode\")) {\n");
        sb.append("            System.out.println(serializeLinkedList((ListNode) result));\n");
        sb.append("        } else {\n");
        sb.append("            System.out.println(result);\n");
        sb.append("        }\n");
        sb.append("    }\n\n");

        addLinkedListHelpers(sb);
    }

    private void addLinkedListHelpers(StringBuilder sb) {
        // buildLinkedList helper
        sb.append("    private static ListNode buildLinkedList(String input, String subtype) {\n");
        sb.append("        if (input == null || input.trim().isEmpty() || input.trim().equals(\"[]\") || input.equals(\"null\")) return null;\n");
        sb.append("        input = input.trim();\n");
        sb.append("        if (input.startsWith(\"[\") && input.endsWith(\"]\")) input = input.substring(1, input.length() - 1);\n");
        sb.append("        String[] parts = input.split(\"[,\\\\s]+\");\n");
        sb.append("        List<ListNode> nodes = new ArrayList<>();\n");
        sb.append("        for (String p : parts) {\n");
        sb.append("            if (!p.trim().isEmpty()) nodes.add(new ListNode(Integer.parseInt(p.trim())));\n");
        sb.append("        }\n");
        sb.append("        if (nodes.isEmpty()) return null;\n");
        sb.append("        for (int i = 0; i < nodes.size(); i++) {\n");
        sb.append("            if (i < nodes.size() - 1) nodes.get(i).next = nodes.get(i+1);\n");
        sb.append("            if (subtype.contains(\"doubly\") && i > 0) nodes.get(i).prev = nodes.get(i-1);\n");
        sb.append("        }\n");
        sb.append("        if (subtype.contains(\"circular\")) {\n");
        sb.append("            ListNode last = nodes.get(nodes.size() - 1);\n");
        sb.append("            ListNode first = nodes.get(0);\n");
        sb.append("            last.next = first;\n");
        sb.append("            if (subtype.equals(\"doubly_circular\")) first.prev = last;\n");
        sb.append("        }\n");
        sb.append("        return nodes.get(0);\n");
        sb.append("    }\n\n");

        // serializeLinkedList helper
        sb.append("    private static String serializeLinkedList(ListNode head) {\n");
        sb.append("        if (head == null) return \"null\";\n");
        sb.append("        StringBuilder res = new StringBuilder();\n");
        sb.append("        Set<ListNode> visited = new HashSet<>();\n");
        sb.append("        ListNode curr = head;\n");
        sb.append("        while (curr != null && !visited.contains(curr)) {\n");
        sb.append("            visited.add(curr);\n");
        sb.append("            if (res.length() > 0) res.append(\" \");\n");
        sb.append("            res.append(curr.val);\n");
        sb.append("            curr = curr.next;\n");
        sb.append("            if (curr == head) break;\n");
        sb.append("            if (visited.size() > 1000) break; // Protection\n");
        sb.append("        }\n");
        sb.append("        return res.toString();\n");
        sb.append("    }\n");
    }

    private void addListNodeClass(StringBuilder sb) {
        sb.append("\nclass ListNode {\n");
        sb.append("    public int val;\n");
        sb.append("    public int data;\n");
        sb.append("    public ListNode next;\n");
        sb.append("    public ListNode prev;\n");
        sb.append("    public ListNode(int x) {\n");
        sb.append("        this.val = x;\n");
        sb.append("        this.data = x;\n");
        sb.append("    }\n");
        sb.append("}\n");
    }
}
