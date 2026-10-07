# GameOptimizer V2

Aplicativo Android seguro para monitorar desempenho e sugerir otimizações para jogos, sem mexer em configurações sensíveis do sistema.

## O que o app faz
- monitora memória RAM disponível
- verifica temperatura da CPU quando disponível
- lê nível da bateria
- mostra recomendações seguras para melhorar a experiência de jogo
- evita ações destrutivas ou invasivas no dispositivo

## Estrutura do projeto
- `app/src/main/java/com/gameoptimizer/v12/MainActivity.kt`
- `app/src/main/java/com/gameoptimizer/v12/PerformanceMonitor.kt`
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/AndroidManifest.xml`

## Como compilar
1. Abra o projeto em Android Studio.
2. Sincronize com Gradle.
3. Execute em emulador ou dispositivo Android.

## Regras de segurança
- não altera arquivos do sistema
- não usa permissões perigosas
- não força encerramento de processos do sistema
- apenas mede e aconselha o usuário

## Observação
Este é um projeto seguro e funcional como base para um app de otimização de jogos, com foco em estabilidade e respeito ao hardware do celular.
