#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>

struct Code {
    char lhs[10];
    char op1[10];
    char op[5];
    char op2[10];
};

int isNumber(char str[]) {
    for (int i = 0; str[i] != '\0'; i++) {
        if (!isdigit(str[i]))
            return 0;
    }
    return 1;
}

int main() {
    struct Code code[20];
    int n;

    printf("Enter number of three address statements: ");
    scanf("%d", &n);

    printf("Enter statements in form: t1 = a + b\n");

    for (int i = 0; i < n; i++) {
        scanf("%s = %s %s %s", code[i].lhs, code[i].op1, code[i].op, code[i].op2);
    }

    printf("\nOptimized Three Address Code:\n");

    for (int i = 0; i < n; i++) {

        // Constant Folding
        if (isNumber(code[i].op1) && isNumber(code[i].op2)) {
            int a = atoi(code[i].op1);
            int b = atoi(code[i].op2);
            int result = 0;

            if (strcmp(code[i].op, "+") == 0)
                result = a + b;
            else if (strcmp(code[i].op, "-") == 0)
                result = a - b;
            else if (strcmp(code[i].op, "*") == 0)
                result = a * b;
            else if (strcmp(code[i].op, "/") == 0 && b != 0)
                result = a / b;

            printf("%s = %d\n", code[i].lhs, result);
        }

        // Algebraic Simplification
        else if (strcmp(code[i].op, "+") == 0 && strcmp(code[i].op2, "0") == 0) {
            printf("%s = %s\n", code[i].lhs, code[i].op1);
        }
        else if (strcmp(code[i].op, "-") == 0 && strcmp(code[i].op2, "0") == 0) {
            printf("%s = %s\n", code[i].lhs, code[i].op1);
        }
        else if (strcmp(code[i].op, "*") == 0 && strcmp(code[i].op2, "1") == 0) {
            printf("%s = %s\n", code[i].lhs, code[i].op1);
        }
        else if (strcmp(code[i].op, "*") == 0 && strcmp(code[i].op2, "0") == 0) {
            printf("%s = 0\n", code[i].lhs);
        }

        // Common Sub-expression Elimination
        else {
            int found = 0;

            for (int j = 0; j < i; j++) {
                if (strcmp(code[i].op1, code[j].op1) == 0 &&
                    strcmp(code[i].op, code[j].op) == 0 &&
                    strcmp(code[i].op2, code[j].op2) == 0) {

                    printf("%s = %s\n", code[i].lhs, code[j].lhs);
                    found = 1;
                    break;
                }
            }

            if (!found) {
                printf("%s = %s %s %s\n",
                       code[i].lhs,
                       code[i].op1,
                       code[i].op,
                       code[i].op2);
            }
        }
    }

    return 0;
}