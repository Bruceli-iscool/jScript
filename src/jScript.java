import java.util.*;
import java.io.*;
public class jScript {
    public static ArrayList<String> lines = new ArrayList<>();
    public static ArrayList<String> out = new ArrayList<>();
    public static ArrayList<String> functionOut = new ArrayList<>();
    public static void main(String args[]) throws IOException{
        if (args.length < 1) {
            System.err.println("jScript Error: No Input Files.");
            System.exit(0);
        }
        compile(args[0]);
    }
    public static void compile(String path) throws IOException{
        File f= new File(path);
        Scanner s = new Scanner(f);
        try {
            while(s.hasNextLine()) {
                lines.add(s.nextLine());
            }
        } catch (Exception e) {
            System.err.println("jScript: Fatal Internal Error");
            System.exit(0);
        }
        s.close();
        while(!lines.isEmpty()) {
            ArrayList<String> tokens = lex(lines.get(0));
            String output="";
            if (!tokens.isEmpty()) {
                String current = tokens.get(0);
                tokens.remove(0);
                while(true) {
                    if (current.matches("println")) {
                        output+=" System.out.println ";
                    } else if (current.matches("var")||current.matches("const")) {
                        if (current.matches("const")) {
                            output+=" final ";
                        }
                        if (tokens.isEmpty()) System.err.println("jScript Error: Expected identifier but recieved nothing.");
                        tokens.remove(0);
                        current = tokens.get(0);
                        tokens.remove(0);
                        output += " "+current+" ";
                        if (tokens.isEmpty()) System.err.println("jScript Error: Expected ';' or '=' but recieved nothing.");
                        current = tokens.get(0);
                        tokens.remove(0);
                        if (current.matches(";")) {
                            output+=";";
                            break;
                        } else {
                            output+=current;
                        }
                        if (tokens.isEmpty()) System.err.println("jScript Error: Expected value but recieved nothing.");
                        current = tokens.get(0);
                        tokens.remove(0);
                        output+=current;
                        if (tokens.isEmpty()) System.err.println("jScript Error: Expected ';' but recieved nothing.");
                        current = tokens.get(0);
                        tokens.remove(0);
                        output+= current;
                    } else if (current.matches("function")) {
                        // todo
                    } else {
                        output += current;
                    }
                    if (tokens.isEmpty()) break;
                    current=tokens.get(0);
                    tokens.remove(0);
                }
            }
            out.add(output);
            lines.remove(0);
        }
        out.add(0, "public static void main(String args[]){");
        out.add(0, "public class out{");
        out.add(0,"import java.util.*;");
        out.add("}");
        out.addAll(functionOut);
        out.add("}");
        FileWriter writer = new FileWriter("out.java");
        for (String g:out) {
            writer.write(g);
        }
        writer.close();
    }
    public static ArrayList<String> lex(String line) {
        ArrayList<String> result = new ArrayList<String>();
        String z = "";
        boolean ifString = false;
        String p = line;
        for (int i = 0; i < p.length(); i++) {
            char c = p.charAt(i);
            if (c == '-' && !ifString && z.isEmpty() && i + 1 < p.length() && Character.isDigit(p.charAt(i + 1))) {
                z += c;
                continue;
            }
            switch (c) {
                case '(': case ')': case ';': case '=': case '+': case '-': case '*': case '/':case '{': case '}':case ':':case '%':
                    if (!z.isEmpty()) {
                        result.add(z);
                        z = "";
                    }
                    result.add(String.valueOf(c));
                    break;
                case '"':
                    if (!z.isEmpty() && ifString) {
                        ifString = false;
                        result.add(z);
                        z = "";
                    } else {
                        ifString = true;
                    }
                    result.add("\"");
                    break;
                case ' ':
                    if (!z.isEmpty() && !ifString) {
                        result.add(z);
                        z = "";
                    } else if (ifString) {
                        z += c;
                    }
                    break;
                default:
                    z += c;
                    break;
            }
        }
        if (!z.isEmpty()) {
            result.add(z);
        }
        return result;
    }
}