#include "main.h"
#include "tm1637.h"

volatile uint32_t tickCount;
uint32_t last_display_update;
uint16_t counter = 0;
char lastKey;                   
uint32_t lastScanTime;          

#define LED_ON()   (GPIOA->ODR |=  (1 << 5))
#define LED_OFF()  (GPIOA->ODR &= ~(1 << 5))

static int8_t  firstValue = -1;
static int8_t  secondValue = -1;
static char  operation;
char ops[] = {'+', '-', '*', '/'};
static uint16_t displayValue;
static char  prevKey;

void osSystickHandler(void) {
  tickCount++;
}

void initGPIO() {
  RCC->AHBENR |= RCC_AHBENR_GPIOAEN | RCC_AHBENR_GPIOBEN;

  GPIOA->MODER = (GPIOA->MODER & ~(3 << 10)) | (1 << 10);
  GPIOA->OTYPER &= ~(1 << 5);
  GPIOA->OSPEEDR |= (1 << 10);
}

void initSysTick() {
  SysTick->LOAD = 47999;
  SysTick->VAL = 0;
  SysTick->CTRL = (1 << 2) | (1 << 1) | (1 << 0);
}

void resetAll(){
  counter = 0;
  firstValue = -1;
  secondValue = -1;
}

static void handleKey(char k) {
  if (k >= '0' && k <= '9') {
    if (firstValue == -1){
      firstValue = k - '0';
      printf("Введено первое число %d\n", firstValue);
    } else if (secondValue == -1){
      secondValue = k - '0';
      printf("Введено второе число %d\n", secondValue);
    }
    return;
  }

  if (k == '#') {
    if (firstValue != -1 && secondValue != -1 && counter != 0){
      if (operation == '/' && secondValue == 0){
        printf("Ошибка: деление на 0\n");
        resetAll();
        return;
      }
      if (operation == '+'){
        displayValue = firstValue + secondValue;
      } else if(operation == '-'){
        displayValue = firstValue - secondValue;
      } else if(operation == '*'){
        displayValue = firstValue * secondValue;
      } else {
        displayValue = firstValue / secondValue;
      }
    printf("%d %c %d = %d\n", firstValue, operation, secondValue, displayValue);
    }
    resetAll();
    return;
  }

  if (k == '*') {
    operation = ops[counter%4];
    counter += 1;
    printf("Выбрана операция %c\n", operation);
    return;
  }
}

int main(void) {
  initGPIO();
  initSysTick();
  initKeyboard();
  tm1637_init();

  printf("'#' = start\n'*' = opration choice\n");

  while (1) {
    scanKeyboard();
    if (lastKey != prevKey) {
      prevKey = lastKey;
      if (lastKey) handleKey(lastKey);
    }
    tm1637_display_number(displayValue);
  }

  return 0;
}
