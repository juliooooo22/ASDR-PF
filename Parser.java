import java.util.List;
import java.util.NoSuchElementException;

public class Parser implements parser_ {
    private final List<Token> tokens;
    private Token currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentToken = tokens.get(0);
    }

    public String parse() {
        try {
            program();
            match(TipoToken.EOF);
            System.out.println("Programa válido");
            return"Programa válido";
        } catch (Parse_E e) {
            System.out.println("Programa no válido");
            System.err.println(e.getMessage());
            return "Programa no válido";
        }
    }

    void program() throws Parse_E {
        declaration();
    }

    void declaration() throws Parse_E {
        switch (currentToken.getTipo()){
            case FUN:
                fun_decl();
                declaration();
                break;
            case VAR:
                var_decl();
                declaration();
                break;
            case FOR:
            case IF:
            case PRINT:
            case RETURN:
            case WHILE:
            case LEFT_BRACE:
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG:
            case MINUS:
                statement();
                declaration();
                break;
            default:
                //Asumimos epsilon
        }
    }

    void functions() throws Parse_E {
        if (currentToken.getTipo() == TipoToken.IDENTIFIER) {
            function();
            functions();
        }
        // si no es IDENTIFIER, asumimos épsilon
    }

    //Otras (Gramática)
    void function() throws Parse_E {
        match(TipoToken.IDENTIFIER);
        match(TipoToken.LEFT_PAREN);
        parameters_opc();
        match(TipoToken.RIGHT_PAREN);
        block();
    }

    void parameters_opc() throws Parse_E {
        if (currentToken.getTipo() == TipoToken.IDENTIFIER) {
            parameters();
        }
        // si no es IDENTIFICADOR, asumimos épsilon
    }

    void parameters() throws Parse_E {
        match(TipoToken.IDENTIFIER);
        parameters_2();
    }

    void parameters_2() throws Parse_E {
        if (currentToken.getTipo() == TipoToken.COMMA) {
            match(TipoToken.COMMA);
            match(TipoToken.IDENTIFIER);
            parameters_2();
        }
        // si no es COMA, asumimos épsilon
    }

    //Declaraciones (Gramática)
    void fun_decl() throws Parse_E {
        match(TipoToken.FUN);
        function();
    }

    void var_decl() throws Parse_E {
        match(TipoToken.VAR);
        match(TipoToken.IDENTIFIER);
        var_init();
        match(TipoToken.SEMICOLON);
    }

    void var_init() throws Parse_E {
        if (currentToken.getTipo() == TipoToken.EQUAL) {
            match(TipoToken.EQUAL);
            expression();
        }
        // si no es ASIGNAR, asumimos épsilon
    }

    //Sentencias-Statement
    void statement() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                expr_stmt();
                break;
            case FOR:
                for_stmt();
                break;
            case IF:
                if_stmt();
                break;
            case PRINT:
                print_stmt();
                break;
            case RETURN:
                return_stmt();
                break;
            case WHILE:
                while_stmt();
                break;
            case LEFT_BRACE:
                block();
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba el inicio de una declaración o sentencia pero se encontró un " + currentToken.getTipo());
        }
    }

    void expr_stmt() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                expression();
                match(TipoToken.SEMICOLON);
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void for_stmt() throws Parse_E {
        match(TipoToken.FOR);
        match(TipoToken.LEFT_PAREN);
        for_stmt_1();
        for_stmt_2();
        for_stmt_3();
        match(TipoToken.RIGHT_PAREN);
        statement();
    }

    void for_stmt_1() throws Parse_E {
        switch (currentToken.getTipo()) {
            case VAR:
                var_decl();
                break;
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                expr_stmt();
                break;
            case SEMICOLON:
                match(TipoToken.SEMICOLON);
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void for_stmt_2() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                expression();
                match(TipoToken.SEMICOLON);
                break;
            case SEMICOLON:
                match(TipoToken.SEMICOLON);
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void for_stmt_3() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                expression();
                break;
            default:
                //Asumimos epsilon
        }
    }

    void if_stmt() throws Parse_E {
        match(TipoToken.IF);
        match(TipoToken.LEFT_PAREN);
        expression();
        match(TipoToken.RIGHT_PAREN);
        statement();
        else_statement();
    }

    void else_statement() throws Parse_E {
        if(currentToken.getTipo()==TipoToken.ELSE){
            match(TipoToken.ELSE);
            statement();
        }
        //Asumimos epsilon
    }

    void print_stmt() throws Parse_E {
        match(TipoToken.PRINT);
        expression();
        match(TipoToken.SEMICOLON);
    }

    void return_stmt() throws Parse_E {
        match(TipoToken.RETURN);
        return_exp_opc();
        match(TipoToken.SEMICOLON);
    }

    void return_exp_opc() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                expression();
                break;
            default:
                //Asumimos epsilon
        }
    }

    void while_stmt() throws Parse_E {
        match(TipoToken.WHILE);
        match(TipoToken.LEFT_PAREN);
        expression();
        match(TipoToken.RIGHT_PAREN);
        statement();
    }

    void block() throws Parse_E {
        match(TipoToken.LEFT_BRACE);
        block_decl();
        match(TipoToken.RIGHT_BRACE);
    }

    void block_decl() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FUN:
            case VAR:
            case FOR:
            case IF:
            case PRINT:
            case RETURN:
            case WHILE:
            case LEFT_BRACE:
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                declaration();
                block_decl();
                break;
            default:
                //Asumimos epsilon
        }
    }

    void expression() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                assignment();
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void assignment() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                logic_or();
                assignment_opc();
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void assignment_opc() throws Parse_E {
        if (currentToken.getTipo() == TipoToken.EQUAL) {
            match(TipoToken.EQUAL);
            expression();
        }
        //Asumimos epsilon
    }

    void logic_or() throws Parse_E{
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                logic_and();
                logic_or_2();
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void logic_or_2() throws Parse_E {
        if (currentToken.getTipo() == TipoToken.OR) {
            match(TipoToken.OR);
            logic_and();
            logic_or_2();
        }
        //Asumimos epsilon
    }

    void logic_and() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                equality();
                logic_and_2();
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void logic_and_2() throws Parse_E {
        if (currentToken.getTipo() == TipoToken.AND) {
            match(TipoToken.AND);
            equality();
            logic_and_2();
        }
        //Asumimos epsilon
    }

    void equality() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                comparison();
                equality_2();
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void equality_2() throws Parse_E {
        switch (currentToken.getTipo()) {
            case BANG_EQUAL:
                match(TipoToken.BANG_EQUAL);
                comparison();
                equality_2();
                break;
            case EQUAL_EQUAL:
                match(TipoToken.EQUAL_EQUAL);
                comparison();
                equality_2();
                break;
            default:
                //Asumimos epsilon
        }
    }

    void comparison() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                term();
                comparison_2();
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void comparison_2() throws Parse_E {
        switch (currentToken.getTipo()) {
            case GREATER:
                match(TipoToken.GREATER);
                term();
                comparison_2();
                break;
            case GREATER_EQUAL:
                match(TipoToken.GREATER_EQUAL);
                term();
                comparison_2();
                break;
            case LESS:
                match(TipoToken.LESS);
                term();
                comparison_2();
                break;
            case LESS_EQUAL:
                match(TipoToken.LESS_EQUAL);
                term();
                comparison_2();
                break;
            default:
                //Asumimos epsilon
        }
    }

    void term() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                factor();
                term_2();
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void term_2() throws Parse_E {
        switch (currentToken.getTipo()) {
            case MINUS:
                match(TipoToken.MINUS);
                factor();
                term_2();
                break;
            case PLUS:
                match(TipoToken.PLUS);
                factor();
                term_2();
                break;
            default:
                //Asumimos epsilon
        }
    }

    void factor() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                unary();
                factor_2();
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void factor_2() throws Parse_E {
        switch (currentToken.getTipo()){
            case SLASH:
                match(TipoToken.SLASH);
                unary();
                factor_2();
                break;
            case STAR:
                match(TipoToken.STAR);
                unary();
                factor_2();
                break;
            default:
                //Asumimos epsilon
        }
    }

    void unary() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
                call();
                break;
            case BANG_EQUAL:
                match(TipoToken.BANG_EQUAL);
                unary();
                break;
            case MINUS:
                match(TipoToken.MINUS);
                unary();
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void call() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
                primary();
                call_2();
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void call_2() throws Parse_E {
        switch (currentToken.getTipo()) {
            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                arguments_opc();
                match(TipoToken.RIGHT_PAREN);
                call_2();
                break;
            case DOT:
                match(TipoToken.DOT);
                match(TipoToken.IDENTIFIER);
                call_2();
                break;
            default:
                //Asumimos epsilon
        }
    }
/*void call_opc() throws Parse_E {
        // TODO: implementar según la gramática
    }*/
    void primary() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
                match(TipoToken.FALSE);
                break;
            case TRUE:
                match(TipoToken.TRUE);
                break;
            case NULL:
                match(TipoToken.NULL);
                break;
            case NUMBER:
                match(TipoToken.NUMBER);
                break;
            case STRING:
                match(TipoToken.STRING);
                break;
            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                expression();
                match(TipoToken.LEFT_PAREN);
            case IDENTIFIER:
                match(TipoToken.IDENTIFIER);
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void arguments_opc() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                arguments();
                break;
            default:
                //Asumimos epsilon
        }
    }

    void arguments() throws Parse_E {
        switch (currentToken.getTipo()) {
            case FALSE:
            case TRUE:
            case NULL:
            case NUMBER:
            case STRING:
            case LEFT_PAREN:
            case IDENTIFIER:
            case BANG_EQUAL:
            case MINUS:
                expression();
                arguments_2();
                break;
            default:
                throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba una expresion valida" + currentToken.getTipo());
        }
    }

    void arguments_2() throws Parse_E {
        if (currentToken.getTipo() == TipoToken.COMMA) {
            match(TipoToken.COMMA);
            expression();
           arguments_2();
        }
        //Asumimos epsilon
    }

    void match(TipoToken type) throws Parse_E {
        if (currentToken.getTipo() == type) {
            if(currentToken.getTipo()!=TipoToken.EOF)
                avanzar();
        } else {
            throw new Parse_E("Error en la línea " + currentToken.getLinea() + ". Se esperaba un " + type + " pero se encontró un " + currentToken.getTipo());
        }
    }

    void avanzar() {
        try {
            currentToken = tokens.get(tokens.indexOf(currentToken) + 1);
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException("Se llegó al final de los tokens sin encontrar EOF.");
        }
    }

}

