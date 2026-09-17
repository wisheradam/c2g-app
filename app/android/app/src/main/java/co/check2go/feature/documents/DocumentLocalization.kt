package co.check2go.feature.documents

import androidx.annotation.StringRes
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import co.check2go.R

@Composable
fun DocumentCategory.localizedName(): String = stringResource(stringRes)

fun DocumentCategory.localizedName(context: Context): String = context.getString(stringRes)

@Composable
fun DocumentStatus.localizedLabel(): String = stringResource(stringRes)

@Composable
fun DocumentSort.localizedLabel(): String = stringResource(stringRes)

/** Document type ids remain English-derived and stable; only their presentation is translated. */
@Composable
fun DocumentTypeConfig.localizedName(): String {
    val language = LocalContext.current.resources.configuration.locales[0]?.language
    return name.localizedDocumentTypeName(language)
}

@Composable
fun TravelDocument.localizedTypeName(): String {
    val language = LocalContext.current.resources.configuration.locales[0]?.language
    return customTypeName ?: typeName.localizedDocumentTypeName(language)
}

fun String.localizedDocumentTypeName(language: String?): String =
    if (language == "ru") russianDocumentTypeNames[this] ?: this else this

private val DocumentCategory.stringRes: Int
    @StringRes get() = when (this) {
        DocumentCategory.Identity -> R.string.document_category_identity
        DocumentCategory.Visas -> R.string.document_category_visas
        DocumentCategory.Transportation -> R.string.document_category_transportation
        DocumentCategory.Accommodation -> R.string.document_category_accommodation
        DocumentCategory.Insurance -> R.string.document_category_insurance
        DocumentCategory.Medical -> R.string.document_category_medical
        DocumentCategory.Financial -> R.string.document_category_financial
        DocumentCategory.TripPurpose -> R.string.document_category_trip_purpose
        DocumentCategory.Family -> R.string.document_category_family
        DocumentCategory.Driving -> R.string.document_category_driving
        DocumentCategory.Pets -> R.string.document_category_pets
        DocumentCategory.Customs -> R.string.document_category_customs
        DocumentCategory.Security -> R.string.document_category_security
        DocumentCategory.Itinerary -> R.string.document_category_itinerary
        DocumentCategory.Connectivity -> R.string.document_category_connectivity
        DocumentCategory.Emergency -> R.string.document_category_emergency
        DocumentCategory.Other -> R.string.document_category_other
    }

private val DocumentStatus.stringRes: Int
    @StringRes get() = when (this) {
        DocumentStatus.Valid -> R.string.document_status_valid
        DocumentStatus.ExpiringSoon -> R.string.document_status_expiring
        DocumentStatus.Expired -> R.string.document_status_expired
        DocumentStatus.Future -> R.string.document_status_future
        DocumentStatus.MissingExpiration -> R.string.document_status_missing_expiration
    }

private val DocumentSort.stringRes: Int
    @StringRes get() = when (this) {
        DocumentSort.Expiration -> R.string.documents_sort_expiration
        DocumentSort.Name -> R.string.documents_sort_name
        DocumentSort.DateAdded -> R.string.documents_sort_added
        DocumentSort.Country -> R.string.documents_sort_country
    }

private val russianDocumentTypeNames = mapOf(
    "Passport" to "Паспорт", "Additional Passport" to "Дополнительный паспорт", "Diplomatic Passport" to "Дипломатический паспорт", "Service Passport" to "Служебный паспорт", "Temporary Passport" to "Временный паспорт",
    "National ID Card" to "Национальное удостоверение личности", "Travel Document" to "Проездной документ", "Refugee Travel Document" to "Проездной документ беженца", "Emergency Travel Document" to "Экстренный проездной документ", "Birth Certificate" to "Свидетельство о рождении", "Marriage Certificate" to "Свидетельство о браке", "Name Change Certificate" to "Свидетельство о смене имени", "Citizenship / Naturalization Certificate" to "Свидетельство о гражданстве / натурализации",
    "Visa" to "Виза", "E-Visa" to "Электронная виза", "Visa on Arrival Approval" to "Разрешение на визу по прибытии", "ETA / ESTA / eTA" to "ETA / ESTA / eTA", "Entry Permit" to "Разрешение на въезд", "Exit Permit" to "Разрешение на выезд", "Re-entry Permit" to "Разрешение на повторный въезд", "Immigration Approval Letter" to "Письмо об одобрении иммиграции", "Border / Entry Authorization" to "Разрешение на пересечение границы / въезд", "Work Permit" to "Разрешение на работу", "Study Permit" to "Разрешение на учёбу", "Digital Nomad Permit" to "Разрешение цифрового кочевника", "Temporary Residence Permit" to "Временный вид на жительство", "Permanent Residence Permit" to "Постоянный вид на жительство", "Residence Card" to "Карта резидента", "Refugee / Asylum Document" to "Документ беженца / убежища",
    "Flight Ticket" to "Авиабилет", "Boarding Pass" to "Посадочный талон", "Train Ticket" to "Билет на поезд", "Bus Ticket" to "Билет на автобус", "Ferry Ticket" to "Билет на паром", "Cruise Ticket" to "Билет на круиз", "Public Transport Pass" to "Проездной на общественный транспорт", "Airport Transfer Confirmation" to "Подтверждение трансфера из аэропорта", "Taxi / Private Transfer Booking" to "Бронирование такси / частного трансфера", "Car Rental Confirmation" to "Подтверждение аренды автомобиля", "Vehicle Reservation" to "Бронирование автомобиля", "Parking Reservation" to "Бронирование парковки", "Bicycle / Scooter Rental Confirmation" to "Подтверждение аренды велосипеда / самоката",
    "Hotel Booking Confirmation" to "Подтверждение бронирования отеля", "Apartment Booking" to "Бронирование апартаментов", "Airbnb Booking" to "Бронирование Airbnb", "Hostel Booking" to "Бронирование хостела", "Resort Booking" to "Бронирование курорта", "Camping Reservation" to "Бронирование кемпинга", "Cruise Cabin Confirmation" to "Подтверждение каюты круиза", "Invitation from Host" to "Приглашение от принимающей стороны", "Private Accommodation Confirmation" to "Подтверждение частного размещения", "Proof of Address at Destination" to "Подтверждение адреса в месте назначения",
    "Travel Insurance" to "Туристическая страховка", "Medical Travel Insurance" to "Медицинская туристическая страховка", "Trip Cancellation Insurance" to "Страхование отмены поездки", "Baggage Insurance" to "Страхование багажа", "Accident Insurance" to "Страхование от несчастного случая", "Rental Car Insurance" to "Страхование арендованного автомобиля", "Health Insurance" to "Медицинская страховка", "International Health Insurance" to "Международная медицинская страховка", "Insurance Assistance Card" to "Карточка страховой помощи", "Insurance Policy Certificate" to "Страховой полис",
    "Vaccination Certificate" to "Сертификат о вакцинации", "International Vaccination Certificate" to "Международный сертификат вакцинации", "Health Certificate" to "Справка о состоянии здоровья", "Medical Certificate" to "Медицинская справка", "Doctor’s Letter" to "Письмо врача", "Prescription" to "Рецепт", "Medication List" to "Список лекарств", "Medication Import Authorization" to "Разрешение на ввоз лекарств", "Allergy Information" to "Информация об аллергиях", "Chronic Condition Summary" to "Сведения о хроническом заболевании", "Disability Certificate" to "Справка об инвалидности", "Pregnancy Certificate" to "Справка о беременности", "Fit-to-Fly Certificate" to "Справка о допуске к перелёту", "Medical Device Documentation" to "Документы на медицинское устройство", "Implant / Pacemaker Card" to "Карта импланта / кардиостимулятора", "Blood Type Information" to "Информация о группе крови", "Emergency Medical Information" to "Экстренная медицинская информация",
    "Bank Statement" to "Банковская выписка", "Credit Card Confirmation" to "Подтверждение кредитной карты", "Proof of Funds" to "Подтверждение наличия средств", "Sponsorship Letter" to "Спонсорское письмо", "Salary Confirmation" to "Подтверждение зарплаты", "Financial Guarantee" to "Финансовая гарантия", "Cash Declaration" to "Декларация наличных средств", "Currency Declaration" to "Валютная декларация", "Financial Supporting Document" to "Финансовый подтверждающий документ",
    "Business Invitation" to "Деловое приглашение", "Conference Registration" to "Регистрация на конференцию", "Event Ticket" to "Билет на мероприятие", "Exhibition Registration" to "Регистрация на выставку", "Business Meeting Confirmation" to "Подтверждение деловой встречи", "Employment Letter" to "Справка с работы", "Employer Travel Letter" to "Письмо работодателя о поездке", "University / School Enrollment Letter" to "Письмо о зачислении в университет / школу", "Student ID" to "Студенческий билет", "Study Invitation" to "Приглашение на обучение", "Family Invitation" to "Семейное приглашение", "Tourist Voucher" to "Туристический ваучер", "Tour Booking" to "Бронирование тура", "Pilgrimage Documentation" to "Документы для паломничества",
    "Child Passport" to "Детский паспорт", "Parental Consent to Travel" to "Согласие родителя на поездку", "Notarized Travel Authorization" to "Нотариальное разрешение на поездку", "Custody Document" to "Документ об опеке", "Adoption Certificate" to "Свидетельство об усыновлении", "Guardianship Document" to "Документ об опекунстве", "Consent from Second Parent" to "Согласие второго родителя", "School Travel Permission" to "Разрешение школы на поездку", "Family Relationship Proof" to "Подтверждение родства",
    "Driver’s License" to "Водительское удостоверение", "International Driving Permit" to "Международное водительское удостоверение", "Vehicle Registration" to "Регистрация транспортного средства", "Vehicle Insurance" to "Страхование транспортного средства", "Rental Agreement" to "Договор аренды", "International Motor Insurance / Green Card" to "Международная автостраховка / Зелёная карта", "Temporary Vehicle Import Permit" to "Разрешение на временный ввоз автомобиля", "Road Toll Pass" to "Пропуск для платных дорог", "Border Vehicle Permit" to "Разрешение на пересечение границы на автомобиле",
    "Pet Passport" to "Паспорт питомца", "Rabies Certificate" to "Сертификат от бешенства", "Veterinary Health Certificate" to "Ветеринарная справка", "Microchip Certificate" to "Сертификат микрочипа", "Import Permit" to "Разрешение на ввоз", "Export Permit" to "Разрешение на вывоз", "Airline Pet Booking" to "Бронирование перевозки питомца", "Quarantine Documentation" to "Документы о карантине",
    "Customs Declaration" to "Таможенная декларация", "Temporary Import Document" to "Документ временного ввоза", "ATA Carnet" to "Карнет ATA", "Goods Declaration" to "Декларация товаров", "Medication Declaration" to "Декларация лекарств", "Drone Permit" to "Разрешение на дрон", "Professional Equipment Permit" to "Разрешение на профессиональное оборудование", "Cultural Goods / Art Export Permit" to "Разрешение на вывоз культурных ценностей / искусства", "Other Special Permit" to "Другое специальное разрешение",
    "Background Check" to "Проверка благонадёжности", "Police Clearance Certificate" to "Справка об отсутствии судимости", "Criminal Record Certificate" to "Справка о судимости", "Security Clearance" to "Допуск по безопасности", "Entry Approval" to "Одобрение въезда", "Special Zone Permit" to "Разрешение на специальную зону", "Border Area Permit" to "Разрешение на приграничную зону",
    "Full Trip Itinerary" to "Полный маршрут поездки", "Day-by-Day Plan" to "План по дням", "Tour Operator Confirmation" to "Подтверждение туроператора", "Package Tour Voucher" to "Ваучер пакетного тура", "Excursion Booking" to "Бронирование экскурсии", "Attraction Ticket" to "Билет на достопримечательность", "Museum Ticket" to "Билет в музей", "Theme Park Ticket" to "Билет в тематический парк", "Concert / Event Ticket" to "Билет на концерт / мероприятие", "Restaurant Reservation" to "Бронирование ресторана",
    "eSIM Confirmation" to "Подтверждение eSIM", "SIM Purchase Confirmation" to "Подтверждение покупки SIM", "Roaming Plan" to "Роуминг-план", "Wi-Fi Rental Confirmation" to "Подтверждение аренды Wi-Fi", "Pocket Wi-Fi Booking" to "Бронирование карманного Wi-Fi",
    "Emergency Contacts" to "Экстренные контакты", "Embassy / Consulate Information" to "Информация о посольстве / консульстве", "Passport Copy" to "Копия паспорта", "Visa Copy" to "Копия визы", "Lost Passport Report" to "Заявление об утере паспорта", "Police Report" to "Полицейский отчёт", "Emergency Travel Certificate" to "Экстренное свидетельство на поездку", "Travel Assistance Case Number" to "Номер обращения в службу помощи",
    "Other Travel Document" to "Другой документ для поездки", "Custom Document" to "Свой документ"
)
