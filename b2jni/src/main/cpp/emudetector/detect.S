#ifdef __LP64__
.global detect_arm

detect_arm:
    SUB SP, SP, #0x40
    STP X4, X5, [SP, #0x10]
    STP X6, X7, [SP, #0x20]
    MOV X4, #0
    MOV X7, #0
TAR:
    nop
TAR1:
    ADD X4, X4, #1
    ADD X7, X7, #1
    ADR X5, TAR
    ADR X6, TAR1
    STRH W5, [x6]
    CMP X4, #2
    BGE EXIT
    CMP X7, #2
    BGE EXIT
    B TAR1
EXIT:
    MOV X0, X4
    LDP X4, X5, [SP, #0x10]
    LDP X6, X7, [SP, #0x20]
    ADD SP, SP, #0x40
    ret
#else
.global detect_arm

detect_arm:
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
#endif