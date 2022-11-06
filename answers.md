## Praca w zespołach:

### Autor: _Mateusz Łopaciński_

### 1. Wskaż układ zdarzeń dla zakleszczenia dla NPMKWB na 1 "kolejce" (wait) w Java z synchronized, wait(), notify(), czy W ma tutaj znaczenie (np. `W == 1`)?

#### Założenia:

- liczba konsumentów wynosi przynajmniej 2 oraz jest przynajmniej 1 producent,

lub:

- liczba producentów wynosi przynajmniej 2 oraz jest co najmniej 1 konsument,

#### Przykładowy układ zdarzeń (gdy jest więcej niż 1 konsument):

- do monitora wchodzi wątek konsumenta i blokuje się na pustym buforze (wykonuje operację `wait()`, ponieważ bufor jest pusty),
- następnie, do monitora wchodzi kolejny wątek konsumenta (kolejność wpuszczania wątków do monitora w Javie jest losowa, więc może się tak zdarzyć, że wpuszczony zostanie kolejny konsument) i on także blokuje się na pustym buforze,
- kolejno wpuszczane są pozostałe wątki konsumentów, które się blokują, czekając na zapełnienie przez producenta bufora (a producent jeszcze nie wszedł do monitora),
- do monitora wchodzi wątek producenta, który zapełnia bufor, a następnie wykonuje funkcję `signal()`, wychodzi z monitora i trafia do kolejki wątków czekających na otrzymanie monitora,
- ponieważ wykonana została operacja `signal()`, do monitora mógłby wejść wątek konsumenta, ale sposób przydzielania wątkom monitora w Javie jest niedeterministyczny, więc możliwe jest, że do monitora ponownie wejdzie wątek producenta, który zawiesi się na pełnym buforze,
- jeżeli jest więcej producentów, kolejno do monitora wpuszczani są producenci, którzy również wieszają się na pełnym buforze (ponieważ nie mogą już produkować więcej zasobów),
- finalnie, wieszają się wszystkie wątki (konsumenci na pustym buforze, a następnie producenci na pełnym buforze).

##### UWAGA:

- Opisany powyżej przypadek nie wyczerpuje wszystkich możliwości. Może się zdarzyć tak, że przez jakiś czas program będzie działał prawidłowo, a wątki nie będą się kolejno jeden po drugim wieszać. Możliwa jest sytuacja, w której początkowo zablokowany zostanie konsument na pustym buforze, po czym na zmianę monitor będzie przydzielany producentom i konsumentom, a następnie zostanie zablokowany wątek producenta na pełnym buforze (lub konsumenta na pustym). Wówczas, możliwy jest przypadek, że żaden z wątków, które czekają po wykonaniu operacji `wait()`, nie otrzyma ponownie monitora, lecz do monitora będą wchodzić zawsze pozostałe (niezablokowane) wątki, przez co zablokowane wątki nigdy nie zostaną odblokowane, a niezablokowane wątki będą się kolejno co jakiś czas blokować.

#### Czy `W == 1` ma tutaj znaczenie?

- Nie, `W == 1` nie ma znaczenia i rozmiar bufora może być dowolną dodatnią liczbą całkowitą. Wielkość bufora nie ma znaczenia, ponieważ może się zdarzyć, że producenci zapełnią cały `W`-elementowy bufor, po czym kolejne wątki producentów zaczną się blokować na pełnym buforze. Zwiększenie rozmiaru bufora wpływa jedynie na zmniejszenie prawdopodobieństwa zakleszczenia, ponieważ bufor będzie rzadziej całkowicie zapełniony lub całkowicie pusty niż w przypadku, w którym dysponujemy wyłącznie `1`-elementowym buforem.

### 2. [dla chętnych] Czy w (błędnym) rozwiązaniu, na klasycznym monitorze, z jedną zmienną warunkową, jest możliwe, że P i K są w niej jednocześnie?

- Tak, jest to możliwe.

#### Przykładowy układ zdarzeń

- może się zdarzyć taka sytuacja, w której do monitora najpierw wchodzi konsument, gdy bufor jest pusty i blokuje się na pustym buforze, czekając na jego zapełnienie,
- następnie, do monitora wchodzi wątek producenta i zapełnia bufor, po czym wykonuje operację `signal()` i opuszcza monitor,
- w kolejnym kroku, do monitora wpuszczony zostaje wątek producenta zamiast konsumenta, który wiesza się na pełnym buforze (zapełnionym poprzednio przez producenta),
- po zawieszeniu producenta, do monitora zostaje wpuszczony kolejny wątek producenta zamiast konsumenta i on również się wiesza (nie zostanie wykonana operacja `signal()`, więc zablokowany konsument nie zostanie odblokowany),
- mamy więc sytuację, w której równocześnie czekają wątki producenta i konsumenta,
- jeżeli w ten sposób, zablokowane zostaną wszystkie wątki producentów (lub konsumentów), żaden z nich nie wykona operacji `signal()`, więc żaden z czekających wątków nie zostanie odblokowany.

## Wykonaj zadania:

### 1. Zaimplementuj NPMKWB — rozwiązanie przy jednoelementowych porcjach z wykorzystaniem Lock i Condition:

- implementacja w katalogu `producer-consumer-2-conditions`

### 2. zaimplementuj j.w. dla P i K z losową wielkością wstawianych/pobieranych porcji (problem zagłodzenia):

1. zaimplementuj poprawne rozwiązanie, bez zagłodzenia (4 Condition);

- implementacja w katalogu `producer-consumer-4-conditions`,

2. wskaż (zapisz w komentarzach w kodzie) przykładowy układ zdarzeń dla zakleszczenia przy niewłaściwej długości bufora:

- Właściwa długość bufora:
  - aby nie doszło do zakleszczenia, bufor musi mieć wielkość przynajmniej `2 * M`, gdzie `M` oznacza maksymalną wielkość produkowanej lub konsumowanej jednorazowo porcji,
- Przykładowy układ zdarzeń, w którym dochodzi do zakleszczenia:
  - producent produkuje `P` produktów (`M < P < 2 * M`), następnie wykonuje operację `signal()` i wychodzi z monitora,
  - do monitora wchodzi konsument, który chce pobrać `K` elementów z bufora (`P < K <= 2 * M`). Ponieważ konsument chce pobrać więcej produktów niż jest w buforze, wykonuje operację `wait()` i się wiesza, czekając na zwiększenie liczby elementów w buforze,
  - do monitora wchodzi producent, który chce wyprodukować `P2` produktów (`P2 > 2 * M - P`) i się wiesza na buforze, ponieważ nie ma w nim wystarczającej liczby wolnych slotów, żeby wyprodukować wszystkie produkty,
  - kolejno do monitora wchodzą producenci, którzy chcą wyprodukować więcej produktów niż jest wolnego miejsca w buforze lub konsumenci, którzy chcą skonsumować więcej produktów niż jest obecnie w buforze. Ponieważ żaden producent nie może dopełnić bufora oraz żaden konsument nie może nic zabrać z bufora, zablokowane zostają wszystkie wątki.
- W jaki sposób zwiększenie rozmiaru bufora do `2 * M` pozwala zapobiec zakleszczeniu?
  - po zwiększeniu rozmiaru bufora do `2 * M`, mamy pewność, że wszyscy producenci produkują jednorazowo co najwyżej `M` produktów oraz wszyscy konsumenci konsumują jednorazowo nie więcej niż `M` produktów, - dzięki temu, po wyprodukowaniu przez producenta `P` produktów (`1 <= P <= M`), wciąż jest miejsce w buforze na to, aby ponownie jakiś producent wyprodukował produkty i umieścił je w buforze,
  - nawet, jeżeli każdy konsument konsumuje `K` produktów (`K > P`) i wszyscy konsumenci zostaną początkowo zablokowani ze względu na niewystarczającą liczbę produktów w buforze, wciąż do monitora może wejść jakiś producent, który dołoży produktów do bufora,
  - po dołożeniu przez producenta produktów do bufora, możliwe jest, że zablokowani zostaną wszyscy producenci, ponieważ będą chcieli produkować więcej niż wynosi wolne miejsce w buforze,
  - wówczas, mamy pewność, że do monitora wejdzie jakiś konsument, ponieważ w buforze musi być przynajmniej `M + 1` produktów, gdy producenci są zablokowani, a konsument konsumuje maksymalnie `M` produktów równocześnie, więc będzie wystarczająco produktów, by konsument mógł je pobrać z bufora,
  - po pobraniu przez konsumenta produktów z bufora, odblokowani zostaną niektórzy producenci, którzy ponownie będą mogli zapełniać bufor,
  - konsumenci i producenci będą się na przemian odblokowywać, więc nigdy nie dojdzie do zakleszczenia (ale może jednak dojść do zagłodzenia, gdy jakiś producent lub konsument nigdy nie zostanie wpuszczony do monitora)

3. wskaż układ zdarzeń dla zagłodzenia przy rozwiązaniu podobnym do zad.1 (2 Condition); pokaż efekt zagłodzenia/braku zagłodzenia poprzez porównanie programów na 2 i 4 Conditions, przy doborze odpowiednich parametrów (liczba wątków, wielkość produkcji/konsumpcji):

#### Przykładowy układ zdarzeń, w którym dojdzie do zagłodzenia:

- do monitora zostaje wpuszczony wątek producenta, który umieszcza pewną ilość produktów w monitorze,
- następnie, do monitora wpuszczony zostaje konsument, który pobiera część produktów,
- ponownie do monitora wchodzi ten sam producent, co poprzednio i produkuje pewną ilość produktów,
- następnie, do monitora wchodzi ten sam, co poprzednio, konsument, który konsumuje wyprodukowane przez producenta produkty,
- sytuacja się powtarza, przez co do monitora wchodzi na przemian ten sam producent i ten sam konsument, a pozostałe wątki nigdy nie dostają monitora.

##### UWAGA:

- Opisany powyżej układ zdarzeń jest przypadkiem skrajnym, w którym tylko 2 wątki dostają monitor, a wszystkie pozostałe są zagłodzone. Bardziej prawdopodobna jest sytuacja, w której tylko kilka wątków nie dostaje monitora, a pozostałe wątki producentów i konsumentów na przemian produkują i konsumują produkty z bufora.
