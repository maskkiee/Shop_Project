# Dokumentacja techniczna — System sklepu internetowego (Shop_Project)

## 1. Wstęp

### 1.1. Cel dokumentacji
Dokumentacja opisuje budowę i działanie systemu sklepu internetowego zrealizowanego w języku **Java 25**. System obsługuje pełen cykl zakupowy: katalog produktów, konfigurację wyrobów, koszyk, składanie zamówień, generowanie faktur, promocje oraz równoległe przetwarzanie zamówień.

### 1.2. Zakres systemu
System realizuje następujące funkcje:
- zarządzanie katalogiem produktów (dodawanie, usuwanie, aktualizacja, wyszukiwanie),
- obsługa trzech typów produktów z dedykowaną konfiguracją (komputer, smartfon, elektronika),
- koszyk zakupowy z walidacją stanu magazynowego,
- przetwarzanie zamówień (synchroniczne i asynchroniczne),
- generowanie faktur,
- system promocji i rabatów,
- persystencja zamówień (do pliku oraz w pamięci),
- konsolowy interfejs użytkownika (CLI).

### 1.3. Technologie
| Element | Technologia |
|---------|-------------|
| Język | Java 25 |
| Budowanie | Maven |
| Testy jednostkowe | JUnit 5 |
| Mockowanie | Mockito |
| Asercje | AssertJ |
| Redukcja boilerplate | Lombok |
| Równoległość | `ExecutorService`, `CompletableFuture` |

---

## 2. Opis działania systemu

### 2.1. Scenariusz podstawowy (składanie zamówienia)
1. Użytkownik przegląda katalog produktów (`ProductManager`).
2. Dla produktu konfigurowalnego (komputer/smartfon) ustala specyfikację.
3. Dodaje produkt do koszyka (`Cart`) — następuje walidacja dostępności (`ensureReadyForCart`).
4. Składa zamówienie (`OrderProcessor.process`) — system weryfikuje stan magazynowy, nalicza rabat, tworzy zamówienie, generuje fakturę i zapisuje dane.
5. Stan magazynowy produktów zostaje zmniejszony (`reduceStock`).

### 2.2. Główne funkcje
- **Katalog** — `addProduct`, `removeProduct`, `update`, `findById`, `findAll`, `findByType`.
- **Konfiguracja** — `Computer.configure(ComputerSpec)`, `Smartphone.configure(SmartphoneSpec)`.
- **Koszyk** — `addItem`, scalanie pozycji, `getTotal` (suma strumieniem).
- **Zamówienia** — `process` (2 przeciążenia), `processAsync` (równolegle).
- **Promocje** — `PromoService.register`, `PromoService.applyTo`.
- **Persystencja** — `OrderRepository` (interfejs), `FileOrderRepository` (plik), `InMemoryOrderRepository` (pamięć).

---

## 3. Architektura i aspekty techniczne

### 3.1. Struktura pakietów
```
com.example.shop
├── app/            ShopApplication (punkt wejścia)
├── cart/           Cart, CartItem
├── cli/            ShopCli (interfejs konsolowy)
├── exceptions/     OrderProcessingException, OutOfStockException, ProductNotFoundException
├── files/          FileOrderRepository (persystencja plikowa)
├── model/          Product, Computer, Smartphone, Electronics, ProductManager, specyfikacje
├── order/          Order, OrderItem, OrderProcessor, InvoiceGenerator, InMemoryOrderRepository
├── persistence/    OrderRepository (interfejs)
├── promo/          PromoService
└── support/        TestProducts, OrderMapping (fabryki / mapowanie)
```

Podział na pakiety realizuje zasadę **jednej odpowiedzialności** — każdy pakiet odpowiada za jeden aspekt biznesowy.

### 3.2. Wzorce i dobre praktyki projektowe
| Rozwiązanie | Zastosowanie | Cel |
|---|---|---|
| **Dziedziczenie + polimorfizm** | `Product` → `Computer`/`Smartphone`/`Electronics` | obsługa typów bez `switch`/`instanceof` w logice |
| **Record (value object)** | `ComputerSpec`, `SmartphoneSpec`, `Customer`, `OrderItem` | immutability, walidacja w konstruktorze |
| **Przeciążenie metod (overload)** | `process(cart, customer)` / `process(cart, customer, promoCode)` | jawny kontrakt zamiast `null`/`boolean` |
| **Wstrzykiwanie zależności (DI)** | konstruktor `OrderProcessor`, `ShopCli` | testowalność, luźne sprzężenie |
| **Guard clauses** | walidacja na wejściu metod | brak zagnieżdżonych `if` |
| **`Optional`** | `findById`, `getSpec` | bezpieczne wyszukiwanie (find-never-get) |
| **Lombok `@Getter`** | `Product`, `CartItem`, `Order` | redukcja boilerplate |

### 3.3. Model danych — hierarchia produktów
```
Product (abstrakcyjna)
├── Computer      → pole spec: ComputerSpec (cpu, ramGB, storageGB)
├── Smartphone    → pole spec: SmartphoneSpec (color, batteryCapacity, accesories)
└── Electronics   → bez dodatkowej konfiguracji
```
- `Product` przechowuje: `id`, `name`, `price`, `stock`.
- Abstrakcyjna metoda `type()` zwraca `ProductType` (enum).
- `ensureReadyForCart()` — waliduje dostępność przed dodaniem do koszyka.
- `reduceStock(amount)` — zmniejsza stan, rzuca `OutOfStockException` przy braku.

Konfigurowalne produkty (`Computer`, `Smartphone`) przechowują **opcjonalną** specyfikację (`Optional<...Spec>`), ustalaną przed zakupem.

---

## 4. Kluczowe moduły — opis techniczny

### 4.1. Moduł produktu (`model`)
Klasa bazowa `Product` definiuje wspólny kontrakt. Podklasy różnią się sposobem konfiguracji. Specyfikacje (`ComputerSpec`, `SmartphoneSpec`) to **recordy** z walidacją w konstruktorze — obiekty immutable, gwarantujące poprawny stan po utworzeniu.

```java
public record ComputerSpec(String cpu, int ramGB, int storageGB) {
    public ComputerSpec {
        if (cpu == null || cpu.isBlank()) throw new IllegalArgumentException("CPU cannot be null or blank");
        if (ramGB < 1) throw new IllegalArgumentException("Ram GB cannot be less than 1GB");
    }
}
```

`ProductManager` przechowuje produkty w `LinkedHashMap` (zachowanie kolejności wstawienia), z walidacją duplikatów przez `putIfAbsent`.

### 4.2. Koszyk (`cart`)
- `addItem(Product, int)` — waliduje produkt, ilość, scalaje pozycje tego samego produktu.
- `getTotal()` — suma `price × quantity` liczona **strumieniem** (`mapToDouble`).
- `ensureReadyForCart()` wywoływane przed dodaniem — blokuje produkty niedostępne.
- `findItem` — odnajduje istniejącą pozycję (scalanie ilości).

### 4.3. Zamówienia i przetwarzanie (`order`)
`OrderProcessor` to serce systemu. Odpowiada za:
1. walidację koszyka,
2. rezerwację stanu magazynowego (`reserveStock`),
3. naliczenie rabatu (`PromoService.applyTo`),
4. budowę zamówienia,
5. zapis i generowanie faktury.

**Najważniejsze rozwiązanie — przeciążenie `process`:**
```java
public Order process(Cart cart, Customer customer) {           // pełna cena
    return createOrder(cart, customer, cart.getTotal());
}
public Order process(Cart cart, Customer customer, String promoCode) {  // z rabatem
    double total = promoService.applyTo(promoCode, cart.getTotal());
    return createOrder(cart, customer, total);
}
private Order createOrder(Cart cart, Customer customer, double total) {
    // wspólna logika: walidacja → rezerwacja → zamówienie → faktura → zapis
}
```
Wspólna metoda `createOrder` eliminuje duplikację (DRY), a dwa publiczne przeciążenia dają jawny kontrakt — bez `null` jako „brak rabatu".

### 4.4. Promocje (`promo`)
`PromoService` mapuje kody promocyjne na rabaty (ułamek). Klucz w mapie jest duży (`toUpperCase`) — kody są case-insensitive. Logika:
```java
public double applyTo(String code, double total) {
    if (code == null || code.isBlank()) return total;     // brak kodu = pełna cena
    double discount = promos.getOrDefault(code.toUpperCase(), NO_DISCOUNT);
    return total * (FULL_PRICE - discount);
}
```
Stałe `FULL_PRICE` i `NO_DISCOUNT` zastępują liczby magiczne.

### 4.5. Persystencja (`persistence`, `files`, `order`)
Wzorzec **Repository**:
- `OrderRepository` — interfejs (`save`),
- `FileOrderRepository` — zapis do pliku (`orders.txt` + osobne pliki faktur),
- `InMemoryOrderRepository` — implementacja w pamięci (testy).

Umożliwia to podmianę sposobu składowania bez zmiany logiki biznesowej.

### 4.6. Wielowątkowość (`order`)
`processAsync` przetwarza listę koszyków **równolegle**:
```java
public void processAsync(List<Cart> carts, List<Customer> customers) {
    List<CompletableFuture<Order>> futures = IntStream.range(0, carts.size())
        .mapToObj(i -> CompletableFuture.supplyAsync(() ->
            process(carts.get(i), customers.get(i)), executorService))
        .toList();
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
}
```
- `ExecutorService` — pula wątków (`newFixedThreadPool`).
- `CompletableFuture` — nieblokujące zadania + `allOf().join()` na zakończenie.
- Numeracja zamówień przez **`AtomicInteger`** — bezpieczna współbieżnie.

### 4.7. Interfejs konsolowy (`cli`)
`ShopCli` — menu w pętli, `switch` expression do wyboru akcji (przegląd, dodanie, koszyk, zamówienie). `ShopApplication` inicjalizuje zależności i uruchamia CLI.

---

## 5. Najtrudniejsze aspekty techniczne

### 5.1. Przetwarzanie synchroniczne vs asynchroniczne
**Problem:** zmierzenie i pokazanie przewagi równoległości nad sekwencyjnością przy tej samej logice biznesowej.

**Rozwiązanie:** symulacja wolnej operacji (`emulateExternalWork` — `Thread.sleep`) wewnątrz `process`, przez co przetwarzanie sekwencyjne jest odczuwalnie wolne. `processAsync` rozkłada te opóźnienia na wiele wątków.

**Wniosek:** dla zadań zależnych od I/O (sen, sieć, dysk) równoległość daje realny zysk wydajnościowy — testy mierzą czas dla 100 i 1000 zamówień.

### 5.2. Bezpieczeństwo współbieżności (thread-safety)
**Problem:** wiele wątków zapisuje jednocześnie do repozytorium i odpytuje stan magazynowy.

**Rozwiązania:**
- `InMemoryOrderRepository` — metody `synchronized` (atomowy zapis),
- `AtomicInteger` do numeracji zamówień (bez wyścigów),
- `ExecutorService` zarządza pulą wątków.

**Ograniczenie:** rezerwacja stanu magazynowego nie jest w pełni synchronizowana — koszyki w testach używają niezależnych produktów, aby uniknąć wyścigu na wspólnym stanie.

### 5.3. Kontrakt metody bez parametrów-przełączników
**Problem:** metoda ma działać w dwóch trybach (z rabatem / bez), ale nie wolno stosować `null` ani `boolean` jako przełącznika (zła czytelność, ryzyko błędów).

**Rozwiązanie:** **przeciążenie metod** (overload) + wspólna metoda prywatna. Każde przeciążenie ma jednoznaczny sens, kompilator pilnuje poprawności.

### 5.4. Obsługa wyjątków
Własna hierarchia wyjątków rozdziela typy błędów:
- `ProductNotFoundException` — brak produktu (zawiera `productId`),
- `OutOfStockException` — za mały stan magazynowy,
- `OrderProcessingException` — błąd przetwarzania/zapisu zamówienia.

Wyjątki są **unchecked** (`RuntimeException`) — nie zaśmiecają sygnatur, a komunikaty są jednoznaczne.

### 5.5. Immutable value objects (recordy)
Zastosowanie recordów dla specyfikacji i danych klienta gwarantuje **niemienność** — walidacja w konstruktorze zapobiega stanom niepoprawnym. `List.copyOf` w recordzie zabezpiecza przed modyfikacją z zewnątrz.

---

## 6. Testowanie

### 6.1. Podejście
- **Testy jednostkowe** (JUnit 5 + AssertJ) dla każdego modułu.
- Nazwy testów jako **zdania** opisujące zachowanie (`@DisplayName`).
- Struktura **given-when-then**; jeden test = jedna teza.

### 6.2. Zakres testów (według modułów TASK)
| Obszar | Co jest testowane |
|--------|-------------------|
| Model produktów | tworzenie, walidacja, konfiguracja |
| ProductManager | CRUD, wyszukiwanie, duplikaty |
| Cart | dodawanie, scalanie, suma, walidacja |
| OrderProcessor | tworzenie zamówienia, faktura, promocje, wyjątki |
| Persystencja plikowa | zapis orders.txt + faktury (`@TempDir`) |
| Wielowątkowość | `processAsync`, porównanie sync vs async |

### 6.3. Mockowanie
Mockito symuluje zależności (repozytoria, serwisy), co izoluje testowaną logikę. Używane mechanizmy: `ArgumentCaptor` (przechwytywanie argumentów), `InOrder` (weryfikacja kolejności), `thenAnswer` (dynamiczne wyniki).

### 6.4. Testy wydajności
Osobny test porównuje czas przetwarzania **synchronicznego** i **asynchronicznego** dla dużej liczby zamówień (100, 1000). Wynik potwierdza przewagę równoległości. Zastosowano `@Timeout`, aby testy nie zawieszały się przy błędach.

---

## 7. Podsumowanie i wnioski

### 7.1. Osiągnięcia
- Kompletny cykl zakupowy: katalog → koszyk → zamówienie → faktura.
- Czysta architektura z podziałem na pakiety i wzorcami projektowymi.
- Pełne testy jednostkowe, w tym testy wydajności i persystencji.
- Równoległe przetwarzanie zamówień (`CompletableFuture`).

### 7.2. Ograniczenia
- Brak interfejsu graficznego (tylko CLI) oraz integracji z bazą danych.
- Persystencja plikowa bez transakcyjności i obsługi współbieżnego zapisu do pliku.

### 7.3. Możliwe dalsze prace
- zastąpienie persystencji plikowej bazą danych (np. H2/PostgreSQL),
- udostępnienie systemu przez REST API,
- front-end (web/desktop),
- pełna obsługa transakcyjna i blokady optymistyczne dla stanu magazynowego.
