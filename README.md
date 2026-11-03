# Sklep internetowy — dokumentacja projektu

## Wprowadzenie
System sklepu internetowego napisany w Javie. Pozwala zarządzać produktami, składać zamówienia i naliczać rabaty.

## Technologie
- **Java 25** — język i środowisko
- **Lombok** — mniej boilerplate (gettery)
- **JUnit 5 + Mockito** — testy jednostkowe i mockowanie
- **AssertJ** — czytelne asercje w testach

## Jak to działa
1. Klient przegląda produkty (katalog).
2. Dodaje wybrane do koszyka.
3. Składa zamówienie — system sprawdza dostępność, liczy cenę, dolicza rabat.
4. Zamówienie jest przetwarzane (równolegle lub synchronicznie), zapisywane do pliku, gotowe do dalszej obsługi.

## Klasy i ich zadania

**Product** (rekord) — podstawowy produkt: id, nazwa, cena, stan. Typy podrzędne: **Computer**, **Smartphone**, **Electronics** — każdy ze swoją specyfikacją (CPU/RAM, kolor/bateria) i walidacją danych w konstruktorze.

**ProductManager** — obsługa produktów: dodawanie, usuwanie, aktualizacja, wyszukiwanie.

**Cart** — koszyk. Zbiera produkty dodane przez użytkownika (`addItem`) i na ich podstawie przygotowuje dane do zamówienia.

**Order** — zamówienie. Na podstawie produktów z koszyka tworzy gotowe zamówienie do przetworzenia.

**OrderProcessor** — główne serce systemu. Przetwarza koszyk na zamówienie. Ma przeciążone `process` (z rabatem / bez) oraz `processAsync` (przetwarzanie równoległe). Tu dzieje się walidacja, naliczanie ceny i zapis.

**OrderRepository** — interfejs zapisu danych o zamówieniach.

**FileOrderRepository** — konkretna realizacja: zapisuje zamówienie do pliku `.txt` ze wszystkimi danymi.

**PromoService** — przygotowuje kod rabatowy, który obniża cenę zamówienia.

**OrderMapping, TestProducts** — klasy pomocnicze: mapują obiekty albo generują przykładowe dane (do testów).

**CLI (ShopCli)** — interfejs konsolowy: przeglądanie produktów, dodawanie do koszyka, składanie zamówienia.

## Testy
Kod objęty testami jednostkowymi (JUnit 5, Mockito, AssertJ) — sprawdzają produkty, koszyk, zamówienia i promocje.