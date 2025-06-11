Реализуем бизнес логику для работы с сетью, что нужно сделать:
- Согласно с документацией https://developers.thecatapi.com/view-account/ylX4blBYT9FaoVd6OhvR?report=bOoHBz-8t реализовать модель данных CatImageModel
- Реализовать CatApiService  интерфейс для работы с библиотекой Retrofit
- Реализовать CatRepository
- Реализовать NetworkModule для предоставления Retrofit сервиса, на промежуточных шагах нужно предоставить Json сериализатор, OkHttp клиент и сам Retrofit
- Реализовать ViewModel логику
- Посмотреть как работает библиотека для загрузки картинок coil в CatCard - AsyncImage
Произвести рефакторинг проекта:
- Добавить domain уровень с use case и чистой архитектурой
- Добавить Dependency injection через Hilt (NetworkModule должен быть переработан)