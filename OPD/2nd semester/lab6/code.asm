ORG 0x00
V0: WORD $DEFAULT , 0x180
V1: WORD $INT1, 0x180
V2: WORD $INT2, 0x180
V3: WORD $DEFAULT , 0x180
V4: WORD $DEFAULT , 0x180
V5: WORD $DEFAULT , 0x180
V6: WORD $DEFAULT , 0x180
V7: WORD $DEFAULT , 0x180
DEFAULT: IRET

ORG 0x20
INT1: LD X  ;f(x)=4X+6 на ВУ-1
      HLT   ;проверяем
      ADD X
      ADD X
      ADD X 
      ADD #6
      OUT 0x2
      IRET

INT2: LD X
      HLT
      
      IN 0x4  ;чтение с ВУ-2
      SXTB
      OR X     ;ИЛИ-НЕ блок
      NEG
      
      CALL CHECK

      HLT

      ST X
      IRET

CHECK: 
CHECK_MIN:
	CMP min
	BPL CHECK_MAX
	LD min
	JUMP RETURN
CHECK_MAX:
	CMP max
	BEQ RETURN
	BLT RETURN
	LD min

RETURN: RET

ORG 0x4F
X: WORD ?
min: WORD 0xFFDF
max: WORD 0x1E

START: DI
       CLA
       OUT 0x1 ;запрет прерываний для неиспользуемых устройств
       OUT 0x7
       OUT 0xB
       OUT 0xE
       OUT 0x12
       OUT 0x16
       OUT 0x1A
       OUT 0x1E
       
       LD #0x9
       OUT 0x3

       LD #0xA
       OUT 0x5

       EI
       JUMP MAIN

MAIN: DI
      LD X
      INC
      CALL CHECK
      ST X
      EI
      JUMP MAIN
