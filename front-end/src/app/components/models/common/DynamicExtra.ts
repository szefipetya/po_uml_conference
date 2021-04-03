class DynamicExtra {
  //create_method==individual, contained
  //temp_id ->kifelé
  //old_id ->befelé
}
/*
  individual esetén a szerver oldalon a session objectet
   magának hozza létre, lockolja
   kér új id-t és beszúrja magát a szülőbe.
   válaszként az action-ba elküldi az old id-t, hogy a
   kliens oldalon ki tudja cserélni az Action Handler.

   contained esetén a szervernek szintén elküldi magát és a temp_id-t
   ilyenkor a szülőbe nem kell beinjektálni, és sessiont sem kell létrehozni.
   csak update id és visszaküldemi. kliens oldalon pedig kicserélni a
   map-ban a régit az uj id-ra és kész.
*/
