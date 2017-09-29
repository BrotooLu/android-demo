.global detectArm

detectArm:
    PUSH { R4, R5, R6, R7, LR }
    MOV R7, #0
    MOV R6, PC
    MOV R4, #0
    ADD R7, R7, #1
    LDR R5, [R6]
TAR:
    ADD R4, R4, #1
    MOV R6, PC
    SUB R6, R6, #12
    STR R5, [R6]
    CMP R4, #2
    BGE EXIT
    CMP R7, #2
    BGE EXIT
    B TAR
EXIT:
    MOV R0, R4
    POP { R4, R5, R6, R7, PC }