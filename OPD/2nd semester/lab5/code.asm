org 0x15d
CLA

main: 
CALL read ; читаем первый символ
SWAB ; Сдвигаем его в старший байт
ST (sym_ptr) ; сохраняем

SWAB ; проверка на стоп символ
CMP stop_sym
BEQ exit

CALL read ; читаем второй символ
OR (sym_ptr) ; соединяем с первым символом
ST (sym_ptr)+ ; сохраняем

AND mask ; проверка на стоп символ
CMP stop_sym
BEQ exit

JUMP main

exit: HLT
sym_ptr: word 0x55e
mask: word 0x00FF
stop_sym: word 0x0A

read:   IN 7            ; Проверка готовности ВУ-3
        AND #0x40
        BEQ read      ; Если не готов, то ждём
        IN 6               ; Читаем символ
        RET               ; Возврат