.global detectArm

detectArm:
    PUSH { R4, R5, R6, R7, R8, LR }
    MOV R7, #0
    MOV R8, PC
    MOV R4, #0
    ADD R7, R7, #1
    LDR R5, [R8]
TAR:
    ADD R4, R4, #1
    MOV R8, PC
    suB R8, R8, #12
    STR R5, [R8]
    CMP R4, #2
    BGE EXIT
    CMP R7, #2
    BGE EXIT
    B TAR
EXIT:
    MOV R0, R4
    POP { R4, R5, R6, R7, R8, PC }