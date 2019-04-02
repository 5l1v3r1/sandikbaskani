2019 Yerel Seçim Sonuçları / Hatalı Sandıklar 
===================


CHP'nin seçim sonuçlarını YSK sisteminden aldığı hali ile yayınladığı adresten 
bu repodaki program ile sonuçlar otomatik olarak ayıklanmıştır.

Şu an için program aşağıdaki algoritma ile çalışmaktadır.

* İstanbul ilini seçer
* Tüm ilçelerde tek tek gezer
* Bulguğu her sandık için sandık sonuç sayfasındaki verileri ayıklar.
* Geçerli oy sayısını bulur.
* Partilere dağıtılmış oy sayısını bulur .
* Eğer partilere dağıtılmış oy sayısı ile , geçerli oy sayısı farklı ise
    * AKP oyu sıfır ise dağıtılmamış tüm oyları AKP hanesine yazar.
	* CHP oyu sıfır ise dağıtılmamış tüm oyları CHP hanesine yazar.
* Sorunlu sandıklar için sonuçları ekrana basar.

2019-04-02 15:21:39.183 | http proxy initialized..

----------


2019-04-02 15:21:40.324 | Giriş yapıldı
