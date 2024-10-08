package tools;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GenerateAST {
    public static void main(String[] args) throws IOException {
//        if(args.length !=1){
//            System.err.println("Usage: generate_ast <output_directory>");
//            System.exit(64);
//        }
        String outDir = "/Users/rohinjoshi/Work/codes/Jalang/src/jasper/";
        defineAst(outDir, "Expr", Arrays.asList(
                "Assign : Token name, Expr value",
                "Binary   : Expr left, Token operator, Expr right",
                "Call : Expr callee , Token paren , List<Expr> arguments",
                "Get : Expr object, Token name",
                "Set: Expr object, Token name, Expr value",
                "Super: Token keyword, Token method",
                "This : Token keyword",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Logical  : Expr left, Token operator, Expr right",
                "Unary    : Token operator, Expr right",
                "Variable : Token name"
        ));
        defineAst(outDir, "Stmt", Arrays.asList(
                "Block: List<Stmt> statements",
                "Expression : Expr expression",
                "Class : Token name, Expr.Variable superclass," + " List<Stmt.Function> methods",
                "Function   : Token name, List<Token> parameters," +
                        " List<Stmt> body",
                "If         : Expr condition, Stmt then," + " Stmt elseBranch",
                "Print      : Expr expression",
                "Return     : Token keyword, Expr value",
                "Var        : Token name, Expr initializer",
                "While      : Expr condition, Stmt body"
        ));
    }
    private static void defineAst(
            String outDir, String baseName, List<String> types
    ) throws IOException{
        String path = outDir + "/" + baseName + ".java";
        PrintWriter pw = new PrintWriter(path, "UTF-8");
        pw.println("package jasper;");
        pw.println();
        pw.println("import java.util.*;");
        pw.println();
        pw.println("abstract class "+baseName + "{");
        defineVisitor(pw, baseName, types);
        for(String type : types){
            String[] temp = type.split(":");
            String className = temp[0].trim();
            String fields = temp[1].trim();
            defineType(pw,baseName,className,fields);

        }
        pw.println();
        pw.println("    abstract <R> R accept(Visitor<R> visitor);");
        pw.println("}");
        pw.close();

    }

    private static void defineVisitor(PrintWriter pw, String baseName, List<String> types) {
        pw.println(" interface Visitor<R> {");
        for(String type: types){
            String typeName = type.split(":")[0].trim();
            pw.println(" R visit" + typeName + baseName +"(" + typeName + " " + baseName.toLowerCase() + ");");
        }
        pw.println("    }");
    }

    private static void defineType(
            PrintWriter writer, String baseName, String className, String fieldList
    ){
        writer.println(" static class "+ className + " extends "+ baseName + " {");
        writer.println("    " + className + "("+ fieldList + ") {");
        String[] fields = fieldList.split(", ");
        for(String field : fields){
            String name = field.split(" ")[1];
            writer.println("      this." + name + " = " + name + ";");
        }
        writer.println("    }");
        writer.println();
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("      return visitor.visit" +
                className + baseName + "(this);");
        writer.println("    }");
        writer.println();
        for (String field : fields) {
            writer.println("    final " + field + ";");
        }
        writer.println("  }");
    }
}
