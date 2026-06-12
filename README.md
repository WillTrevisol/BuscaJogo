# BuscaJogo

**Disciplina:** Tópicos em Dispositivos Móveis  
**Instituição:** Instituto Federal de Educação, Ciência e Tecnologia de São Paulo (IFSP) - Câmpus São Carlos.

## Sobre o Projeto
O **BuscaJogo** é um aplicativo Android desenvolvido como atividade prática principal da disciplina de Tópicos em Dispositivos Móveis. O objetivo do projeto é explorar o potencial de ferramentas de IA generativa no ciclo de desenvolvimento de software, criando um sistema funcional que integra consumo de APIs externas e armazenamento local de dados.

O app funciona como um agregador de informações sobre jogos, permitindo que os usuários descubram jogos populares, busquem títulos específicos, vejam detalhes técnicos e encontrem os melhores preços em lojas digitais.

## Funcionalidades Principais
- **Integração com APIs Externas:**
    - **RAWG API:** Utilizada para obter metadados detalhados de jogos (descrições, imagens, notas do Metacritic, gêneros).
    - **Is There Any Deal (ITAD) API:** Utilizada para buscar preços em tempo real e promoções em diversas lojas (Steam, Epic, Nuuvem, GOG).
- **Armazenamento Local (Room Database):**
    - **Lista de Desejos:** O usuário pode favoritar jogos que deseja acompanhar.
    - **Coleção:** O usuário pode registrar os jogos que já possui em sua biblioteca.

## Como Utilizar

### Pré-requisitos
- Android Studio Ladybug ou superior.
- Chaves de API para os serviços utilizados:
    1. [RAWG API Key](https://rawg.io/apidocs)
    2. [Is There Any Deal API Key](https://isthereanydeal.com/apps/mine/)

### Configuração
1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/buscajogo.git
   ```
2. No arquivo `local.properties` adicione suas chaves de API:
   ```properties
   RAWG_API_KEY=sua_chave_aqui
   IS_THERE_ANY_DEAL_API_KEY=sua_chave_aqui
   ```
   *Nota: O projeto está configurado para ler estas chaves do `BuildConfig` gerado pelo Gradle.*

3. Sincronize o projeto com o Gradle e execute no seu dispositivo ou emulador.

---

## Relatório de Desenvolvimento (IA Generativa)

Este projeto foi desenvolvido com o auxílio de ferramentas de IA (como o Android Studio Bot/Gemini), seguindo as diretrizes da atividade.

### 1. Tempo e Fases
- **Pesquisa e Planejamento:** ~1 hora. Definição do escopo e escolha das APIs. A IA ajudou a comparar as opções de APIs de jogos.
- **Configuração de Infraestrutura (Retrofit, Room, Hilt):** ~2 horas. A IA gerou a maior parte do código de boilerplate para as interfaces da API e entidades do banco.
- **Desenvolvimento de UI (Compose):** ~4 horas. Grande parte do tempo foi gasto ajustando o layout e comportamentos de scroll, onde a IA foi essencial para gerar componentes complexos rapidamente.
- **Correção de Erros e Polimento:** ~2 horas. Resolução de problemas de compatibilidade de bibliotecas (como o erro de compilação do Room/KSP com Kotlin 2.1.0).

### 2. Divisão de Código
- **IA Generativa:** Criou o código inicial, incluindo modelos de dados, repositórios e estruturas de UI.
- **Manual:** Foram realizados ajustes finos na lógica de negócio, refatoração para melhor legibilidade e correção de bugs de integração que a IA não previu.

### 3. Sentimentos e Percepções
- **Animação:** Ao ver integrações de API funcionando em minutos.
- **Frustração:** Ao lidar com sugestões de bibliotecas incompatíveis ou padrões de código depreciados, exigindo intervenção manual e pesquisa adicional.
- **Reflexão:** A experiência demonstra que a IA não substitui o desenvolvedor, mas atua como uma ferramenta que acelera o trabalho repetitivo.
