# Teoria śladów

## Pojęcia

### 1. akcje, transakcje

- akcja - podstawowa jednostka obliczeń, reprezentująca zmianę stanu systemu. Akcje można interpretować jako dyskretne kroki, które podejmuje system, w celu przejścia z jednego stanu do drugiego. Formalnie, akcją jest funkcja, która przyjmuje bieżący stan systemu i tworzy nowy stan,

- transakcja - podobnie jak w systemach bazodanowych, transakcja oznacza wykonywanie wielu operacji jako całość (poszczególne akcje są traktowane jako pojedyncza akcja, która musi się wykonać w całości albo wcale). Oznacza to, że sekwencja poszczególnych jest wykonywana razem w sposób atomiczny. Dzięki wykorzystaniu transakcji, mamy pewność, że zmiany, jakie spowodowały poszczególne akcje, zostaną cofnięte, jeśli transakcja zakończy się niepowodzeniem lub wykonane zostaną wszystkie akcje, w przypadku powodzenia.

### 2. relacja zależności i niezależności nad alfabetem

- zależność nad alfabetem - oznacza relację pomiędzy poszczególnymi akcjami, w której wykonanie jednej z zależnych akcji może mieć wpływ na wynik działania innej akcji. Ta relacja może zostać opisana jako ograniczenie na zbiór akcji, jakie mogą zostać wykonane w danym momencie.

  np. Jeżeli akcje _A_ oraz _B_ są zależne nad pewnym alfabetem, oznacza to, że wykonanie akcji _A_ wpłynie na zbiór możliwych wyników wykonania akcji _B_ (i odwrotnie, wykonanie akcji _B_, ograniczy zbiór możliwych wyników wykonania akcji _A_).

  Zależność akcji jest wykorzystywana do zapewnienia, że zachowanie systemu jest przewidywalne i jest często stosowana podczas modelowania systemów, w których kolejność wykonywanych akcji ma znaczenie. Zależność akcji stosuje się również w systemach współbieżnych oraz rozproszonych do sprawdzania, czy nie występują deadlocki oraz inne problemy.

- niezależność nad alfabetem - oznacza relację pomiędzy różnymi akcjami w śladzie, w której wykonanie poszczególnej akcji nie wpływa na wynik wykonania innej, niezależnej akcji. Ta relacja może zostać opisana jako brak ograniczeń na zbiór możliwych akcji, które mogą zostać wykonane w danym momencie.

  np. Jeżeli akcje _A_ oraz _B_ są niezależne nad pewnym alfabetem, oznacza to, że wykonanie akcji _A_ nie będzie miało wpływu na zbiór możliwych wyników wykonania akcji _B_ (i odwrotnie).

  Niezależność akcji jest wykorzystywana w systemach, w których kolejność ich wykonania nie jest istotna, a poszczególne akcje mogą być wykonywane niezależnie w sposób współbieżny. Niezależność akcji jest też wykorzystywana w systemach, których poszczególne komponenty mogą być wykonywane niezależnie, bez zakłócania działania innych komponentów systemu.

### 3. ślad wyznaczany przez słowo `w` względem relacji niezależności

- oznacza to, żę ślad jest sekwencją akcji reprezentowanych przez wyraz `w`, gdzie poszczególne akcje są niezależne nad alfabetem. Oznacza to, że wykonanie poszczególnych akcji nie wpłynie na możliwe wyniki wykonania pozostałych akcji w śladzie.

  np. rozważmy alfabet `{a, b, c}` oraz słowo `w = abc`. Wówczas ślad wyznaczany przez słowo `w` względem relacji niezależności jest śladem, w którym akcje, reprezentowane przez litery `a`, `b` oraz `c` są niezależne od siebie.

  Innymi słowy, wykonanie akcji `a` nie ma wpływu na wynik wykonania akcji `b`, a wynik wykonania akcji `b` nie ma wpływu na wykonanie akcji `c`. Oznacza to, że te akcje mogą być wykonywane współbieżnie.

### 4. postać normalna Foaty śladu `[w]`

- In trace theory, a normal form is a specific representation of a trace that satisfies certain constraints or conditions. A normal form is used to simplify the representation of a trace and make it easier to reason about the behavior of a system. Different normal forms can be used depending on the specific properties of the system being modeled.

  Some common normal forms in trace theory include:

  "Foat [w]" which stands for "Finite Observations Automata with Traces", this normal form is used to represent systems where the set of possible traces is finite and well-defined.
  "LTS" which stands for "Labelled Transition System", this normal form is used to represent systems where the set of possible traces is not necessarily finite, but the transitions between states are labelled with specific actions.
  "Kripke Structure", this normal form is used to represent systems where the set of possible traces is not necessarily finite and transitions between states are not labelled.
  In general, a normal form is a way to simplify the representation of a trace and make it easier to reason about the behavior of a system. It allows to focus on the specific properties that are relevant to the problem at hand, and it is used to check the absence of deadlocks and other consistency issues.

  In this normal form, a trace is represented as a finite automaton that is augmented with a trace set, which is represented by the square brackets "[w]". The finite automaton represents the possible states and transitions of the system, while the trace set represents the possible sequences of actions that the system can take.

  The "Foat [w]" normal form is used to represent systems where the set of possible traces is finite and well-defined. The "w" inside the square bracket represent the set of possible traces.

  In this normal form, each state in the automaton corresponds to a set of possible traces, and transitions between states correspond to the execution of a specific action. The trace set represented by the brackets "[w]" is used to specify the set of possible traces that can be generated by the system.

  It is used in formal verification of concurrent and distributed systems to check the absence of deadlocks and other consistency issues. It is also used to model systems where the order in which actions are executed is important and where the overall operation is considered to be atomic.

### 5. graf zależności Diekerta dla słowa `w`, graf w postaci zminimalizowanej

- A Diekert dependency graph for a word 'w' represents the dependencies between the actions in the word 'w' as a directed graph. In this graph, each action in the word 'w' is represented by a node, and the edges between nodes represent the dependencies between actions.

  For example, if the word 'w' is "abc" and action 'a' depends on action 'b' and action 'c', the Diekert dependency graph would have three nodes representing 'a', 'b' and 'c', and directed edges from 'b' and 'c' to 'a'. This means that action 'a' depends on actions 'b' and 'c', and these dependencies are represented by directed edges in the graph.

  The use of a dependency graph in trace theory allows to easily visualize the dependencies between actions in a trace and make it easier to reason about the behavior of a system. It also allows to check the absence of mutual blocking and other consistency issues.

  It is primarily used in formal verification of concurrent and distributed systems to check the absence of deadlocks and other consistency issues. It is also used to model systems where the order in which actions are executed is important, and where the overall operation is considered to be atomic.

#### w postaci zminimalizowanej
- A minimized Diekert dependency graph of a word 'w' is a graph that represents the dependencies between the actions in the word 'w' in a simplified and reduced form. It is obtained by applying minimization techniques on the original Diekert dependency graph.

  Minimization techniques are used to remove redundant or unnecessary information from the graph, while preserving the essential dependencies between actions. This can be achieved by merging nodes that represent equivalent actions, or by removing edges that do not affect the overall behavior of the system.

  The minimized Diekert dependency graph of a word 'w' will have the same set of dependencies between actions as the original graph, but it will be a simpler and more compact representation.

  This can be useful in situations where the original graph is too complex or difficult to understand, and a simplified version is needed for analysis and verification. Also, it can help to improve the performance of algorithms that operate on the graph and it is also useful for reducing the state space in model checking.

  It is primarily used in formal verification of concurrent and distributed systems to check the absence of deadlocks and other consistency issues. It is also used to model systems where the order in which actions are executed is important, and where the overall operation is considered to be atomic.
