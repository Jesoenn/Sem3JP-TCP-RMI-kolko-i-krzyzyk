# Kółko i krzyżyk (klient - serwer)
## Treść programowa:
1. Rozpraszanie obliczeń poprzez wykorzystanie gniazd TCP/IP.
2. Rozpraszanie obliczeń poprzez zdalne wywoływanie procedur. (RMI)
## Funkcjonalności
1. **Hybrydowa komunikacja sieciowa** - użycie zarówno komunikacji poprzez RMI jak i gniazd TCP/IP.
2. **Mechanika gry** - implementacja silnika zarządzającego mechaniką gry w kółko i krzyżyk
- Należy wykorzystać mechanizm RMI w celu nawiązania połączenia między dwoma graczami oraz obsługę mechaniki gry (np. parowanie graczy, inicjalizowanie stanu gry, wymiana ruchów, walidacja I/O).
- Mechanika gry powinna być zaimplementowana w aplikacji serwera.
- Należy przemyśleć argumenty pobierane przez aplikacje JAR serwer/klient.
- Serwer powinen obsługiwać więcej niż jednego gracza w tym samym momencie (np. poprzez realizację gier w dedykowanych pokojach lub tworzenie tokenów poszczególnych sesji wymienianych przez graczy).
- Serwer powinen obliczać statystyki graczy w danej sesji - ilość wygranych, remisów oraz porażek.
- Aplikacja serwera powinna logować w konsoli kluczowe dane (parowanie użytkowników, błędy, koniec gry, itp).
- zalecane java.nio

## GRUPA A
- chat pomiędzy użytkownikami z TCP/IP
- Serwer udostępnia iformacjęo adresach IP użytkowników
![image](https://github.com/user-attachments/assets/e624110e-2d44-481f-a49f-e2ecf4c448df)

## Wymagania
1. Minimum dwie aplikacje (serwer, klient, obserwator*)
2. Zaimplementowane zasady gry, statystyki
3. Komunikacja z wykorzystaniem protokołu RMI
4. Obsługa N klientów (skalowalność)
5. Obsługa mechaniki pokojów (gracze nie są parowami losowego)
6. Implementacja funkcjonalności chatu pomiędzy dwoma graczami z wykorzystaniem mechanizmu gniazd TCP/IP.
