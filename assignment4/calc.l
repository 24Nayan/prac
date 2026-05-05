
%{
#include "y.tab.h"
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
%}

%%

"sqrt"      return SQRT;
"strlen"    return STRLEN;

[0-9]+      {yylval.num = atoi(yytext); return NUMBER; }
[a-zA-Z]+   {yylval.str = strdup(yytext); return ID; } 

\".*\"      {yylval.str = strdup(yytext); return STRING; }

"+"         return '+';
"-"         return '-';
"*"         return '*';
"/"         return '/';
"="         return '=';

"("         return '(';
")"         return ')';

[ \t]     ;   // ignore spaces
\n          return '\n';

.           return yytext[0];
%%

int yywrap() {
    return 1;
}
