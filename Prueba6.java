// Prueba 6:
/*
                Código para calcula la serie de Fibonacci
                */
                var fib = 0;
                var lim = 10;
                var aux = 1;
                                
                for(var init = 1; init <= lim; init = init + 1){
                    print fib;
                    aux = aux + fib;
                    fib = aux - fib;
                }