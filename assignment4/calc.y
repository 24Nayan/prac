%{
#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include<string.h>

void yyerror(const char *s);
int yylex();

int vars[26];   // variables a-z
%}

%union {
    int num;
    char *str;
}

%token <num> NUMBER
%token <str> ID STRING
%token SQRT STRLEN
%left '+' '-'
%left '*' '/' 

%type <num> expr

%%

input:
    line
    | input line
    ;

line:
    expr '\n'         { printf("Result = %d\n", $1); }
    | ID '=' expr '\n' { vars[$1[0]-'a'] = $3; }
    ;

expr:
      NUMBER               { $$ = $1; }
    | ID                   { $$ = vars[$1[0]-'a']; }
    | expr '+' expr        { $$ = $1 + $3; }
    | expr '-' expr        { $$ = $1 - $3; }
    | expr '*' expr        { $$ = $1 * $3; }
    | expr '/' expr        { $$ = $1 / $3; }
    | '(' expr ')'         { $$ = $2; }

    | SQRT '(' expr ')'    { $$ = sqrt($3); }

    | STRLEN '(' STRING ')' { 
        int len = strlen($3) - 2; // remove quotes
        $$ = len;
    }
    ;

%%

int main() {
    printf("Enter expression:\n");
    yyparse();
    return 0;
}

void yyerror(const char *s) {
    printf("Error: %s\n", s);
}
