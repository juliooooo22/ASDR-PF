import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Interprete {

    static boolean existenErrores = false;

    public static void main(String[] args) throws Exception {
        if(args.length > 1) {
            System.out.println("Uso correcto: interprete [archivo.txt]");

            // Convención defininida en el archivo "system.h" de UNIX
            System.exit(64);
        } else if(args.length == 1){
            ejecutarArchivo(args[0]);
        } else{
            try {
                ejecutarPrompt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void ejecutarArchivo(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        ejecutar(new String(bytes, Charset.defaultCharset()));

        // Se indica que existe un error
        if(existenErrores) System.exit(65);
    }

    private static void ejecutarPrompt() throws Exception {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for(;;){
            System.out.print(">>> ");
            String linea = reader.readLine();
            if(linea == null) break; // Presionar Ctrl + D
            ejecutar(linea);
            existenErrores = false;
        }
    }

    //Se modificó a public...
    public static String ejecutar(String source) throws Exception {
        
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scan();
           /*  for(Token token : tokens){
                System.out.println(token);
            }*/
        parser_ psr = new Parser(tokens);
        return psr.parse();

    }

    /*
    El método error se puede usar desde las distintas clases
    para reportar los errores:
    Interprete.error(....);
     */
    static void error(int linea, String mensaje){
        reportar(linea, "", mensaje);
    }

    private static void reportar(int linea, String posicion, String mensaje){
        System.err.println(
                "[linea " + linea + "] Error " + posicion + ": " + mensaje
        );
        existenErrores = true;
    }

}
