%{
#include <stdio.h>
#include <string.h>

char word[20], type[20];

void findPOS(char *input) {
    FILE *fp = fopen("vocab.txt", "r");

    if (fp == NULL) {
        perror("vocab.txt");
        return;
    }

    while (fscanf(fp, "%19s %19s", word, type) == 2) {
        if (strcmp(input, word) == 0) {
            printf("%s is a %s\n", input, type);
            fclose(fp);
            return;
        }
    }

    printf("%s is not found in vocabulary\n", input);
    fclose(fp);
}
%}

%%

[a-zA-Z]+     { findPOS(yytext); }

[ \t\n]+      { /* ignore spaces */ }

.             { printf("%s is unknown\n", yytext); }

%%

int main() {
    printf("Enter sentence:\n");
    yylex();
    return 0;
}

int yywrap() {
    return 1;
}