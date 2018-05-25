# DistributedFileSystem
Serwer (NameNode) prostego rozproszonego systemu plików. 

## Zasada działania
Poprzez web'owy interfejs użytkownik ma dostęp do swojego drzewa katalogów. Umieszczając pliki system rozmieszcza je optymalnie na
dostępnych węzłach danych. Dla każdego pliku dostępna jest możliwość ustawienia na ilu węzłach (w celu redundancji) ma się znajdować.

## Konfiguracja
Dostępne węzły danych podajemy w pliku dfsNameNode/configuration/dataNodes

## Wymagania
 - Serwer wykorzystuje folder dfsNameNode umieszczany w katalogu użytkownika, więc wymagane są uprawnienia do zapisu i odczytu w tym katalogu.
 - Serwer uruchamiany jest na porcie 8090, więc w przypadku chęci dostępu z sieci zewnętrznej musi zostać on otwarty, aczkolwiek nie jest to
 zalecane, system został stworzony do pracy w sieci lokalnej, gdyż nie ma żadnego systemu kont użytkowniku i zarazem kontroli dostępu do 
 zasobów
