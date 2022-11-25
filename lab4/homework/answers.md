### Autor: _Mateusz Łopaciński_

### 1. Na czym polega zagłodzenie przy rozwiązaniu zadania na dwóch Conditions

- Zagłodzenie na 2 conditions objawia się tym, że przynajmniej jeden z wątków otrzymuje zasoby (dostaje monitor) znacznie rzadziej niż pozostałe wątki (a w najgorszym przypadku nie dostaje go w ogóle).

#### Dlaczego, w przypadku korzystania z 2 Conditions, obserwujemy zagłodzenie?

- Korzystamy tylko z 2 zmiennych (pierwsza związana jest z konsumentami, a druga z producentami),
- Nie mamy żadnego mechanizmu w Javie, który gwarantuje kolejność przyznawania monitora wątkom, które się ubiegają o jego przyznanie,
- Losowy sposób przyznawania monitora wątkom może spowodować, że niektóre wątki będą otrzymywały monitor rzadziej od pozostałych. Najrzadziej monitor zostanie przydzielony producentom, którzy produkują największą liczbę zasobów oraz konsumentom, którzy konsumują największą liczbę zasobów. Wynika to stąd, że przez bufor będzie dopełniany przez małych producentów i jednocześnie z bufora najczęściej będą pobierać zasoby mali konsumenci, przez co liczba elementów w buforze będzie się przez większość czasu utrzymywać na takim poziomie, że duży producent nie będzie w stanie umieścić wszystkich jednorazowo produkowanych zasobów i będzie czekał na zwolnienie bufora. Podobnie, duży konsument będzie czekać na dopełnienie bufora do takiego poziomu, żeby liczba zasobów osiągnęła tyle, ile jednorazowo pobiera z bufora duży konsument.

#### Przykładowy układ zdarzeń, w którym dojdzie do zagłodzenia:

- do monitora zostaje wpuszczony wątek producenta, który umieszcza pewną ilość produktów w monitorze,
- następnie, do monitora wpuszczony zostaje konsument, który pobiera część produktów,
- ponownie do monitora wchodzi ten sam producent, co poprzednio i produkuje pewną ilość produktów,
- następnie, do monitora wchodzi ten sam, co poprzednio, konsument, który konsumuje wyprodukowane przez producenta produkty,
- sytuacja się powtarza, przez co do monitora wchodzi na przemian ten sam producent i ten sam konsument, a pozostałe wątki nigdy nie dostają monitora.

### 2. Jak można wykazać/zmierzyć zagłodzenie - można pokazać wyniki śledzenia dla obu rozwiązań i porównać (na 2 i na 4 Conditions)?

#### Sposób mierzenia zagłodzenia

- Najłatwiejszym sposobem jest wypisywanie co jakiś czas na konsolę podsumowania, zawierającego informacje o tym, ile razy monitor został przydzielony poszczególnym wątkom. Aby śledzić, ile razy wątek otrzymał monitor, wystarczy w każdym z wątków stworzyć licznik, którego wartość będziemy zwiększać wtedy, gdy odpowiednio konsument wykona operację `consume()`, a producent wykona operację `produce()`. Następnie, można co pewien odstęp czasu, wypisywać na konsolę zestawienie, zawierające nazwy przypisane wątkom oraz liczby, oznaczające to, ile razy dany wątek otrzymał monitor.
- Jeżeli liczba dostępów do monitora przez dany wątek będzie znacząco niższa od liczb, oznaczających, ile razy pozostałe wątki otrzymały monitor, może to oznaczać, że ten wątek jest zagłodzony.

#### Porównanie wyników śledzenia na 2 Condition i na 4 Conditions:

- Oba pomiary zostały przeprowadzone dla tych samych danych, z tą samą liczbą konsumentów i producentów oraz z takimi samymi ustawieniami tych wątków,
- W przypadku 2 Conditions bardzo dokładnie widać zagłodzenie konsumenta o numerze 0, który nie otrzymał monitora ani razu, podczas gdy pozostali konsumenci otrzymali monitor po kilkadziesiąt tysięcy razy. Wynika to stąd, że konsument 0 konsumuje bardzo duże liczby zasobów z bufora, a mali producenci nie są w stanie tak bardzo zapełnić bufora, ponieważ mali konsumenci zabierają zasoby z bufora zanim w buforze znajdzie się tyle zasobów, ile konsumuje duży konsument,
- W przypadku 4 Conditions nie obserwujemy już zagłodzenia. Ponieważ wątki otrzymują zasoby w takiej kolejności, w jakiej się po nie zgłaszają, nie dojdzie do sytuacji, w której bufor nigdy nie będzie miał liczby zasobów konsumowanych przez dużego konsumenta.

#### Dlaczego nie obserwujemy zagłodzenia, gdy wykorzystujemy 4 conditions?

- W przypadku, gdy korzystamy z 4 Conditions, monitor jest przydzielany wątkom w takiej kolejności, w jakiej się one ubiegały o dostęp do monitora. Pozwala to na wyeliminowanie sytuacji, w której pewne wątki otrzymują dostęp do monitora znacznie rzadziej niż pozostałe (w szczególności duzi producenci/konsumenci mają takie same szanse na otrzymanie monitora jak mali konsumenci/producenci),
- Poza 4 Conditions, wykorzystujemy jeszcze 2 zmienne boolowskie, które wskazują na to, czy odpowiednio pierwszy producent lub konsument, który czekał na dostęp do monitora, nadal czeka na wejście do monitora. Dopóki pierwszy ubiegający się o monitor producent/konsument czeka na dostęp do monitora, wieszamy wszystkie pozostałe wątki producentów/konsumentów (odpowiednio `remainingProducers.await()` i `remainingConsumers.await()`),
- Dopóki liczba zasobów w buforze jest nieodpowiednia (za duża, żeby producent mógł dołożyć produkowane przez siebie zasoby lub za mała, żeby konsument mógł pobrać z bufora konsumowane przez siebie jednorazowo zasoby), wstrzymujemy odpowiednio wątek producenta lub konsumenta, czekający jako pierwszy na dostęp do monitora,
- Po osiągnięciu odpowiedniej liczby zasobów przez bufor, umożliwiamy dostęp do monitora czekającemu wątkowi (producenta lub konsumenta, w zależności od tego, który z wątków zostanie zwolniony), wątek ten produkuje/konsumuje zasoby, a następnie sygnalizuje możliwość dostępu do monitora odpowiednim wątkom (gdy w monitorze był wątek producenta, sygnalizuje on możliwość wejścia do monitora pierwszemu czekającemu konsumentowi oraz pozostałym producentom; jeżeli jednak był to wątek konsumenta, sygnalizuje on możliwość wejścia do monitora pierwszemu czekającemu producentowi oraz pozostałym konsumentom). Jednocześnie, wychodzący z monitora wątek, zmienia wartość zmiennej boolowskiej na przeciwną (tzn. producent ustawia `isFirstProducerWaiting` na `false`, a konsument ustawia `isFirstConsumerWaiting` na `false`), umożliwiając tym samym, zwolnienie pozostałych producentów/konsumentów,
- Po zwolnieniu pozostałych producentów/konsumentów, losowy z nich (w Javie nie można przewidzieć, który) wątek producenta/konsumenta staje się pierwszym czekającym na dostęp do zasobów, a pozostałe znów się wieszają.

### 3. Jak dochodzi do zakleszczenia przy niewłaściwej długości bufora?

#### Właściwa długość bufora

- aby nie doszło do zakleszczenia, bufor musi mieć wielkość przynajmniej `2 * M - 1`, gdzie `M` oznacza maksymalną wielkość produkowanej lub konsumowanej jednorazowo porcji.

#### Przykładowy układ zdarzeń, w którym dochodzi do zakleszczenia:

- producent produkuje `P` produktów (`M < P < 2 * M - 1`), następnie wykonuje operację `signal()` i wychodzi z monitora,
- do monitora wchodzi konsument, który chce pobrać `K` elementów z bufora (`P < K <= 2 * M - 1`). Ponieważ konsument chce pobrać więcej produktów niż jest w buforze, wykonuje operację `wait()` i się wiesza, czekając na zwiększenie liczby elementów w buforze,
- do monitora wchodzi producent, który chce wyprodukować `P2` produktów (`P2 > 2 * M - 1 - P`) i się wiesza, ponieważ nie ma w nim wystarczającej liczby wolnych slotów, żeby wyprodukować wszystkie produkty,
- kolejno do monitora wchodzą producenci, którzy chcą wyprodukować więcej produktów niż jest wolnego miejsca w buforze lub konsumenci, którzy chcą skonsumować więcej produktów niż jest obecnie w buforze. Ponieważ żaden producent nie może dopełnić bufora oraz żaden konsument nie może nic zabrać z bufora, zablokowane zostają wszystkie wątki.

#### W jaki sposób zwiększenie rozmiaru bufora do `2 * M - 1` pozwala zapobiec zakleszczeniu?

- po zwiększeniu rozmiaru bufora do `2 * M - 1`, mamy pewność, że wszyscy producenci produkują jednorazowo co najwyżej `M` produktów oraz wszyscy konsumenci konsumują jednorazowo nie więcej niż `M` produktów,
- dzięki temu, po wyprodukowaniu przez producenta `P` produktów (`1 <= P <= M`), wciąż jest miejsce w buforze na to, aby ponownie jakiś producent wyprodukował produkty i umieścił je w buforze,
- nawet, jeżeli każdy konsument konsumuje `K` produktów (`K > P`) i wszyscy konsumenci zostaną początkowo zablokowani ze względu na niewystarczającą liczbę produktów w buforze, wciąż do monitora może wejść jakiś producent, który dołoży produktów do bufora,
- po dołożeniu przez producenta produktów do bufora, możliwe jest, że zablokowani zostaną wszyscy producenci, ponieważ będą chcieli produkować więcej niż wynosi wolne miejsce w buforze,
- wówczas, mamy pewność, że do monitora wejdzie jakiś konsument, ponieważ w buforze musi być przynajmniej `M + 1` produktów, gdy producenci są zablokowani, a konsument konsumuje maksymalnie `M` produktów równocześnie, więc będzie wystarczająco produktów, by konsument mógł je pobrać z bufora,
- po pobraniu przez konsumenta produktów z bufora, odblokowani zostaną niektórzy producenci, którzy ponownie będą mogli zapełniać bufor,
- konsumenci i producenci będą się na przemian odblokowywać, więc nigdy nie dojdzie do zakleszczenia (ale może jednak dojść do zagłodzenia, gdy jakiś producent lub konsument nigdy nie zostanie wpuszczony do monitora)

### 4. Jak dochodzi do zakleszczenia przy rozwiązaniu "poprawnym" na 4 Conditions z zastosowaniem hasWaiters()

#### Ogólny opis problemu

- Problem wynika stąd, że w przypadku rozwiązania z 4 Conditions i 2 wartościami boolowskimi, wartości boolowskie oznaczały, że pierwszy producent/konsument czeka na dostęp do monitora. Z tego powodu, ubiegające się o monitor wątki, wieszały się już na 1. pętli, sprawdzającej, czy jest już inny wątek producenta/konsumenta, który czeka na dostęp do zasobów. Odpowiednia zmienna boolowska miała zmienianą wartość na `false` dopiero wtedy, gdy pierwszy, czekający na dostęp do monitora producent/konsument, opuścił ten monitor (wykonał wszystkie operacje i poinformował odpowiednie wątki o możliwość ubiegania się o dostęp do monitora),
- W przypadku, gdy korzystamy z `hasWaiters()`, istnieje możliwość, że będzie istniał wątek konsumenta, który będzie niezauważony podkradał zasoby (lub producenta, który poza kolejnością będzie dokładał zasoby do bufora).

#### Przykładowy układ zdarzeń

- Opis na zrzucie ekranu
