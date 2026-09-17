package co.check2go.feature.documents

enum class DocumentField {
    Name, CountryOfIssue, DocumentNumber, IssueDate, ExpirationDate, ValidFrom, ValidUntil,
    IssuingAuthority, Citizenship, DestinationCountry, Notes, Primary, TravelRecommendations
}

data class DocumentTypeConfig(
    val id: String,
    val name: String,
    val category: DocumentCategory,
    val fields: Set<DocumentField>,
    val supportsPrimary: Boolean = false
)

enum class DocumentCategory(val displayName: String, val icon: String, val typeNames: List<String>) {
    Identity("Identity & Citizenship", "ID", listOf(
        "Passport", "Additional Passport", "Diplomatic Passport", "Service Passport", "Temporary Passport",
        "National ID Card", "Travel Document", "Refugee Travel Document", "Emergency Travel Document",
        "Birth Certificate", "Marriage Certificate", "Name Change Certificate", "Citizenship / Naturalization Certificate"
    )),
    Visas("Visas & Immigration", "V", listOf(
        "Visa", "E-Visa", "Visa on Arrival Approval", "ETA / ESTA / eTA", "Entry Permit", "Exit Permit",
        "Re-entry Permit", "Immigration Approval Letter", "Border / Entry Authorization", "Work Permit",
        "Study Permit", "Digital Nomad Permit", "Temporary Residence Permit", "Permanent Residence Permit",
        "Residence Card", "Refugee / Asylum Document"
    )),
    Transportation("Transportation", "T", listOf(
        "Flight Ticket", "Boarding Pass", "Train Ticket", "Bus Ticket", "Ferry Ticket", "Cruise Ticket",
        "Public Transport Pass", "Airport Transfer Confirmation", "Taxi / Private Transfer Booking",
        "Car Rental Confirmation", "Vehicle Reservation", "Parking Reservation", "Bicycle / Scooter Rental Confirmation"
    )),
    Accommodation("Accommodation", "A", listOf(
        "Hotel Booking Confirmation", "Apartment Booking", "Airbnb Booking", "Hostel Booking", "Resort Booking",
        "Camping Reservation", "Cruise Cabin Confirmation", "Invitation from Host",
        "Private Accommodation Confirmation", "Proof of Address at Destination"
    )),
    Insurance("Insurance", "I", listOf(
        "Travel Insurance", "Medical Travel Insurance", "Trip Cancellation Insurance", "Baggage Insurance",
        "Accident Insurance", "Rental Car Insurance", "Health Insurance", "International Health Insurance",
        "Insurance Assistance Card", "Insurance Policy Certificate"
    )),
    Medical("Medical", "M", listOf(
        "Vaccination Certificate", "International Vaccination Certificate", "Health Certificate", "Medical Certificate",
        "Doctor’s Letter", "Prescription", "Medication List", "Medication Import Authorization", "Allergy Information",
        "Chronic Condition Summary", "Disability Certificate", "Pregnancy Certificate", "Fit-to-Fly Certificate",
        "Medical Device Documentation", "Implant / Pacemaker Card", "Blood Type Information", "Emergency Medical Information"
    )),
    Financial("Financial & Proof of Funds", "$", listOf(
        "Bank Statement", "Credit Card Confirmation", "Proof of Funds", "Sponsorship Letter", "Salary Confirmation",
        "Financial Guarantee", "Cash Declaration", "Currency Declaration", "Financial Supporting Document"
    )),
    TripPurpose("Trip Purpose", "P", listOf(
        "Business Invitation", "Conference Registration", "Event Ticket", "Exhibition Registration",
        "Business Meeting Confirmation", "Employment Letter", "Employer Travel Letter",
        "University / School Enrollment Letter", "Student ID", "Study Invitation", "Family Invitation",
        "Tourist Voucher", "Tour Booking", "Pilgrimage Documentation"
    )),
    Family("Family & Minors", "F", listOf(
        "Child Passport", "Birth Certificate", "Parental Consent to Travel", "Notarized Travel Authorization",
        "Custody Document", "Adoption Certificate", "Guardianship Document", "Consent from Second Parent",
        "School Travel Permission", "Family Relationship Proof"
    )),
    Driving("Driving & Vehicle", "D", listOf(
        "Driver’s License", "International Driving Permit", "Vehicle Registration", "Vehicle Insurance",
        "Rental Agreement", "International Motor Insurance / Green Card", "Temporary Vehicle Import Permit",
        "Road Toll Pass", "Border Vehicle Permit"
    )),
    Pets("Pets", "PET", listOf(
        "Pet Passport", "Vaccination Certificate", "Rabies Certificate", "Veterinary Health Certificate",
        "Microchip Certificate", "Import Permit", "Export Permit", "Airline Pet Booking", "Quarantine Documentation"
    )),
    Customs("Customs & Special Items", "C", listOf(
        "Customs Declaration", "Temporary Import Document", "ATA Carnet", "Goods Declaration", "Currency Declaration",
        "Medication Declaration", "Drone Permit", "Professional Equipment Permit",
        "Cultural Goods / Art Export Permit", "Other Special Permit"
    )),
    Security("Security & Entry Requirements", "S", listOf(
        "Background Check", "Police Clearance Certificate", "Criminal Record Certificate", "Security Clearance",
        "Entry Approval", "Special Zone Permit", "Border Area Permit"
    )),
    Itinerary("Travel Itinerary & Reservations", "R", listOf(
        "Full Trip Itinerary", "Day-by-Day Plan", "Tour Operator Confirmation", "Package Tour Voucher",
        "Excursion Booking", "Attraction Ticket", "Museum Ticket", "Theme Park Ticket", "Concert / Event Ticket",
        "Restaurant Reservation"
    )),
    Connectivity("Communication & Connectivity", "SIM", listOf(
        "eSIM Confirmation", "SIM Purchase Confirmation", "Roaming Plan", "Wi-Fi Rental Confirmation", "Pocket Wi-Fi Booking"
    )),
    Emergency("Emergency Documents", "SOS", listOf(
        "Emergency Contacts", "Embassy / Consulate Information", "Passport Copy", "Visa Copy", "Lost Passport Report",
        "Police Report", "Emergency Travel Certificate", "Travel Assistance Case Number"
    )),
    Other("Other", "+", listOf("Other Travel Document", "Custom Document"));

    val types: List<DocumentTypeConfig>
        get() = typeNames.map { name ->
            DocumentTypeConfig(
                id = "${this.name.lowercase()}-${name.slug()}",
                name = name,
                category = this,
                fields = defaultFields,
                supportsPrimary = this == Identity || name.contains("Permit") || name.contains("Visa")
            )
        }

    private val defaultFields: Set<DocumentField>
        get() = when (this) {
            Identity -> setOf(DocumentField.Name, DocumentField.CountryOfIssue, DocumentField.DocumentNumber, DocumentField.IssueDate, DocumentField.ExpirationDate, DocumentField.IssuingAuthority, DocumentField.Citizenship, DocumentField.Notes, DocumentField.Primary, DocumentField.TravelRecommendations)
            Visas -> setOf(DocumentField.Name, DocumentField.CountryOfIssue, DocumentField.DocumentNumber, DocumentField.ValidFrom, DocumentField.ValidUntil, DocumentField.DestinationCountry, DocumentField.Notes, DocumentField.Primary, DocumentField.TravelRecommendations)
            Transportation, Accommodation, Itinerary, Connectivity -> setOf(DocumentField.Name, DocumentField.ValidFrom, DocumentField.ValidUntil, DocumentField.DestinationCountry, DocumentField.Notes)
            Insurance, Medical -> setOf(DocumentField.Name, DocumentField.CountryOfIssue, DocumentField.DocumentNumber, DocumentField.IssueDate, DocumentField.ExpirationDate, DocumentField.Notes)
            else -> setOf(DocumentField.Name, DocumentField.CountryOfIssue, DocumentField.DocumentNumber, DocumentField.IssueDate, DocumentField.ExpirationDate, DocumentField.DestinationCountry, DocumentField.Notes)
        }
}

object DocumentCatalog {
    val categories: List<DocumentCategory> = DocumentCategory.entries
    val types: List<DocumentTypeConfig> = categories.flatMap { it.types }
    fun type(category: DocumentCategory, id: String): DocumentTypeConfig? = category.types.firstOrNull { it.id == id }
}

private fun String.slug(): String = lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
