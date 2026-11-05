# JustMeme API — Demonstração do Tema 4

**Programação de Dispositivos Móveis 2026/27 · Tema 4: APIs remotas e processamento assíncrono · Grupo [Número do teu Grupo]**

App Android desenvolvida em **Java**: vai buscar à API aberta "justmeme.wtf" a lista de templates de memes, mostra-a numa lista (com carregamento assíncrono de imagens) e guarda no telemóvel a última lista recebida. Depois de ter sido aberta uma vez com rede, funciona sem rede com os dados guardados.

## Conteúdo do repositório
- `app/` — projeto Android (Java + Views tradicionais)
- `docs/` — protocolo experimental (PDF) e slides da apresentação. **Ler primeiro:** `docs/protocolo_experimental.pdf`

## Versões usadas (não atualizar)
- **Android Studio:** Ladybug (ou versão mais recente compatível)
- **SDKs e Linguagem:** Java 17 · `compileSdk 34` · `minSdk 24` (Android 7.0)
- **Bibliotecas:** Retrofit 2.11 · OkHttp 4.12 · Gson 2.10 · Glide 4.16

## Pré-requisitos
- A API do JustMeme é pública e não precisa de chave (API Key).
- A app usa a permissão `INTERNET` (já declarada no manifesto).
- O *Gradle Sync* precisa de internet na primeira vez.

## Abrir e correr
1. Clonar o repositório (ou `Code` → `Download ZIP` e descompactar).
2. Android Studio → `File` → `Open` → escolher a pasta do projeto.
3. Esperar pelo *Gradle Sync*. Se o Android Studio sugerir atualizar versões de dependências, não aceitar: o protocolo foi testado com as versões atuais.
4. Escolher um emulador ou telemóvel (Android 7.0 ou superior) com acesso à internet e carregar em ▶ **Run**.

## Os Ficheiros Principais (`app/src/main/java/pt/ubi/di/pdm/apresentacaoaula/`)

| Ficheiro | O que faz | Conteúdos do tema abordados |
| :--- | :--- | :--- |
| **`ClienteRede.java`** & **`MemeApiService.java`** | Configuração do cliente Retrofit/OkHttp, timeouts reduzidos para 10s e interface com os verbos HTTP (`@GET`). | REST, HTTP, Retrofit, OkHttp, Timeouts, Interface. |
| **`MemeResponse.java`** & **`MemeTemplate.java`** | Modelos de dados (POJOs) que representam a estrutura da resposta e cada meme individual. | Formato JSON, Desserialização, `@SerializedName`, Gson. |
| **`CacheMemes.java`** | Guarda a lista (convertida para JSON) e a data da gravação usando `SharedPreferences`. | Dados locais, Cache, Serialização manual com Gson. |
| **`UiState.java`** | Classe genérica que representa os três estados possíveis do ecrã (`LOADING`, `SUCCESS`, `ERROR`). | Gestão de Estado. |
| **`MemeViewModel.java`** | Gere o estado do ecrã, pede dados à API em *background* e capta erros de rede. | Assincronismo, `ExecutorService` (Worker Threads), `LiveData` (`postValue`), Tratamento de erros. |
| **`MainActivity.java`** & **`MemeAdapter.java`** | O ecrã que observa o ViewModel e o Adapter que desenha a lista de imagens com a biblioteca Glide. | RecyclerView, Observação de Ciclo de Vida, Indicadores de Progresso. |

## Fluxo
Abrir a app → tenta ler cache local → pede à API → serializa e guarda a nova resposta → atualiza a lista. 
**Se o pedido falhar:** a lista guardada na cache continua no ecrã + mostra uma mensagem do erro traduzida + botão "Tentar de novo".

## Roteiro da demonstração
1. **Abrir a app com rede** → indicador de progresso (loading) → lista de memes com imagens.
2. **Fechar a app, ativar o modo de avião, abrir outra vez** → a lista aparece instantaneamente (vem do armazenamento local) + mensagem "Sem ligação à internet." 
3. **Desligar o modo de avião** → Clicar em "Tentar de novo" → a app faz o pedido e a lista atualiza.
4. **Rodar o ecrã** → nada se perde (o `ViewModel` sobrevive e não repete o pedido de rede em vão).

> **Antes da apresentação:** Ir a `Definições` → `Apps` → `[Nome da App]` → `Armazenamento` → `Limpar dados`, para garantir que o passo 1 mostra o verdadeiro primeiro carregamento.
