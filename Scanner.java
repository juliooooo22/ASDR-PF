import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class Scanner {

    private static final Map<String, TipoToken> palabrasReservadas;
    private static final Map<String, TipoToken> oprelacionales;

    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and",    TipoToken.AND);
        palabrasReservadas.put("else",   TipoToken.ELSE);
        palabrasReservadas.put("false",  TipoToken.FALSE);
        palabrasReservadas.put("for",    TipoToken.FOR);
        palabrasReservadas.put("fun",    TipoToken.FUN);
        palabrasReservadas.put("if",     TipoToken.IF);
        palabrasReservadas.put("null",   TipoToken.NULL);
        palabrasReservadas.put("or",     TipoToken.OR);
        palabrasReservadas.put("print",  TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true",   TipoToken.TRUE);
        palabrasReservadas.put("var",    TipoToken.VAR);
        palabrasReservadas.put("while",  TipoToken.WHILE);

        oprelacionales = new HashMap<>();
        oprelacionales.put("(", TipoToken.LEFT_PAREN);
        oprelacionales.put(")", TipoToken.RIGHT_PAREN);
        oprelacionales.put("{", TipoToken.LEFT_BRACE);
        oprelacionales.put("}", TipoToken.RIGHT_BRACE);
        oprelacionales.put(",", TipoToken.COMMA);
        oprelacionales.put(".", TipoToken.DOT);
        oprelacionales.put(";", TipoToken.SEMICOLON);
        oprelacionales.put("+", TipoToken.PLUS);
        oprelacionales.put("-", TipoToken.MINUS);
        oprelacionales.put("*", TipoToken.STAR);
    }

    private final String source;

    private final List<Token> tokens = new ArrayList<>();
    private int lineActual = 1;

    List<String> se = Arrays.asList("(", ")", "{", "}", ",", ".", ";", "+", "-", ";", "*");
    
    public Scanner(String source){
        this.source = source + " ";
    }

    public List<Token> scan() throws Exception {
        int estado = 0;
        String lexema = "";
        char c;

        for(int i=0; i<source.length(); i++){
            c = source.charAt(i);

            switch(estado){

                case 0: //Estado inicial
                    if(Character.isLetter(c)){
                        estado = 1;
                        lexema += c;
                    }

                    else if(Character.isDigit(c)){
                        estado = 2;
                        lexema += c;
                    }

                    else if(c == '"'){
                        estado = 8;
                        lexema += c;
                    }

                    else if(c == '/'){
                        estado = 10;
                        lexema += c;
                    }

                    else if(se.contains(c+"")){
                        estado = 14;
                        lexema += c;
                    }

                    else if(c == '<'){
                        estado = 15;
                        lexema += c;
                    }

                    else if(c == '>'){
                        estado = 16;
                        lexema += c;
                    }

                    else if(c == '='){
                        estado = 17;
                        lexema += c;
                    }

                    else if(c == '!'){
                        estado = 18;
                        lexema += c;
                    }

                break;

                case 1:
                    if(Character.isLetter(c) || Character.isDigit(c)){ 
                        estado = 1;
                        lexema += c;
                    
                    }else{
                        //Se genera token para identificadores...
                        TipoToken tt = palabrasReservadas.get(lexema);

                        if(tt == null){
                            Token nuevotoken = new Token(TipoToken.IDENTIFIER, lexema, null, lineActual);
                            tokens.add(nuevotoken);

                        }else{
                            Token nuevotoken = new Token(tt, lexema, null, lineActual);
                            tokens.add(nuevotoken);
                        }

                        estado = 0;
                        lexema = "";
                        i--;
                    }

                break;

                case 2:
                    if(Character.isDigit(c)){
                        estado = 2;
                        lexema += c; 
                    }

                    else if(c == '.'){
                        estado = 3;
                        lexema += c;
                    }

                    else if(c == 'E'){
                        estado = 5;
                        lexema += c;
                    }

                    else{
                        //Se genera token para número...
                        Token new_token = new Token(TipoToken.NUMBER, lexema, Integer.valueOf(lexema), lineActual);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;
                    }

                break;

                case 3:
                    if(Character.isDigit(c)){
                        estado = 4;
                        lexema += c;
                    
                    }else{
                        estado = -1; //Estado de aceptación
                        lexema += c;
                    }

                break;

                case 4:
                    if(Character.isDigit(c)){
                        estado = 4;
                        lexema += c;
                    }

                    else if(c == 'E'){
                        estado = 5;
                        lexema += c;
                    }

                    else{
                        //Se genera token para número...
                        Token new_token = new Token(TipoToken.NUMBER, lexema, Double.valueOf(lexema), lineActual);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;
                    }

                break;

                case 5:                    
                if(c == '+' || c == '-'){
                    estado = 6;
                    lexema += c;
                }

                else if(Character.isDigit(c)){
                    estado = 7;
                    lexema += c;
                }

                else{
                    estado = -1; //Estado de aceptación
                    lexema += c;
                }

                break;

                case 6:
                    if(Character.isDigit(c)){
                        estado = 7;
                        lexema += c;
                    }

                    else{
                        estado = -1; //Estado de aceptación
                        lexema += c;
                    }

                break;

                case 7:
                    if(Character.isDigit(c)){
                        estado = 7;
                        lexema += c;
                    }

                    else{
                        //Se genera token para número...
                        Token new_token = new Token(TipoToken.NUMBER, lexema, Double.valueOf(lexema), lineActual);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;
                    }

                break;

                case 8:
                    if(c == '"'){
                        //Se genera token para una cadena...
                        //lexema += c;
                        Token new_token = new Token(TipoToken.STRING, lexema, lexema, lineActual);
                        tokens.add(new_token);

                        estado = 9;
                        //lexema = "";  
                    }

                    else if(c != '\n' && c != '\r'){
                        estado = 9;
                        lexema += c;                    
                    }

                    else{
                        estado = -1; //Estado de aceptación
                        lexema += c;
                    }

                break;

                case 9:                
                    if(c == '"'){
                        //Se genera token para cadena...
                        lexema += c;
                        Token new_token = new Token(TipoToken.STRING, lexema, lexema, lineActual);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                    }

                    else if(c != '\n' && c != '\r'){
                        estado = 9;
                        lexema += c;
                    }

                    else{
                        estado = -1; //Estado de aceptación
                        lexema += c;
                    }

                break;

                case 10:
                    if(c == '*'){
                        estado = 12;
                        lexema += c;
                    }

                    else if(c == '/'){
                        estado = 11;
                        lexema += c;
                    }

                    else{
                        //Se genera token para '/'...
                        Token new_token = new Token(TipoToken.SLASH, lexema, null, lineActual);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;
                    }

                break;

                case 11:
                    if(c != '\n' && c != '\r'){
                        estado = 11;
                        lexema += c;
                    }

                    else{
                        //Se genera token para comentarios...
                        //Token new_token = new Token(TipoToken.COMMENTS, lexema);
                        //tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;
                    }

                break;

                case 12:
                    if(c == '*'){
                        estado = 13;
                        lexema += c;
                    
                    }else{
                        estado = 12;
                        lexema += c;
                    }

                break;

                case 13:
                    if(c == '\n'){
                        estado = 12;
                        lexema += c;
                    }

                    else if(c == '/'){
                        //Se genera token para comentarios...
                        //lexema += c;
                        //Token new_token = new Token(TipoToken.COMMENTS, lexema);
                        //tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                    }

                    else{
                        estado = 13;
                        lexema += c;
                    }

                break;

                case 14:
                    //Se genera token para los operadores relacionales...
                    TipoToken t_token = oprelacionales.get(lexema);
                    Token new_Token = new Token(t_token, lexema, t_token, lineActual);

                    tokens.add(new_Token);

                    estado = 0;
                    lexema = "";
                    i--;

                break;

                case 15:
                    if(c == '='){

                        //Se genera token para '<='...
                        lexema += c;
                        Token new_token = new Token(TipoToken.LESS_EQUAL, lexema, null, lineActual);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                    
                    }else{
                        //Se genera token para '<'...
                        Token new_token = new Token(TipoToken.LESS, lexema, null, lineActual);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;
                    }

                break;

                case 16:
                    if(c == '='){
                        //Se genera token para '>='...
                        lexema += c;
                        Token new_token = new Token(TipoToken.GREATER_EQUAL, lexema, null, lineActual);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                    
                    }else{
                        //Se genera token para '>'...
                        Token new_token = new Token(TipoToken.GREATER, lexema, null, lineActual);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                break;

                case 17:
                    if(c == '='){
                        //Se genera token para '=='...
                        lexema += c;
                        Token new_token = new Token(TipoToken.EQUAL_EQUAL, lexema, null, lineActual);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                    
                    }else{
                        //Se genera token para '='...
                        Token new_token = new Token(TipoToken.EQUAL, lexema, null, lineActual);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                break;  

                case 18:
                    if(c == '='){
                        //Se genera token para '!='
                        lexema += c;
                        Token new_token = new Token(TipoToken.BANG_EQUAL, lexema, null, lineActual);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                    
                    }else{

                        //Se genera token para '!'
                        Token new_token = new Token(TipoToken.BANG, lexema, null, i);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                break;

                //Si se llega al estado muerto se genera un error en Interprete...
                default:
                        Interprete.error(0, lexema);
                        estado = 0;
                        lexema = "";
                        i--;
                break;
            }   
        }
        //Si no se está en Estado de aceptación se manda un Error...
        if(estado != 0)
            Interprete.error(0, lexema);

        tokens.add(new Token(TipoToken.EOF, "", null, lineActual));
        return tokens;
    }   
}