#include <stdio.h>
#include <string.h>
#include <ctype.h>

char expr[50];
int tempCount = 1;

void generateTAC(char op) {
    int i;
    char temp[5];

    for (i = 0; expr[i] != '\0'; i++) {
        if (expr[i] == op) {
            printf("t%d = %c %c %c\n", tempCount, expr[i - 1], expr[i], expr[i + 1]);

            sprintf(temp, "t%d", tempCount);

            expr[i - 1] = temp[0];
            expr[i] = temp[1];
            expr[i + 1] = ' ';

            tempCount++;
        }
    }
}

int main() {
    printf("Enter expression: ");
    scanf("%s", expr);

    printf("\nThree Address Code:\n");

    generateTAC('/');
    generateTAC('*');
    generateTAC('+');
    generateTAC('-');

    return 0;
}