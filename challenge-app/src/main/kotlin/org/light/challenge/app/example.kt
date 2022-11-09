//class LegalPerson(id: EntityID<UUID>) : UUIDEntity(id) {
//    companion object : UUIDEntityClass<LegalPerson>(LegalPersonTable)
//
//    var internalId by LegalPersonTable.internalId
//    var companyId by LegalPersonTable.companyId
//    var active by LegalPersonTable.active
//    var tradeName by LegalPersonTable.tradeName
//    var fantasyName by LegalPersonTable.fantasyName
//    var email by LegalPersonTable.email
//    var cnpj by LegalPersonTable.cnpj
//    var stateRegistration by LegalPersonTable.stateRegistration
//    var muninipalRegistration by LegalPersonTable.muninipalRegistration
//    var address by LegalPersonTable.address
//    val phones by Phone referrersOn Phones.idLegalPerson
//}
//
//object LegalPersonTable : UUIDTable("person.legal_person") {
//    val internalId = long("internal_id").autoIncrement().uniqueIndex()
//    val companyId = uuid("company_id")
//    val active = bool("active")
//    val tradeName = varchar("trade_name", 100)
//    val fantasyName = varchar("fantasy_name", 100)
//    val email = varchar("email", 100)
//    val cnpj = varchar("cnpj", 18)
//    val stateRegistration = varchar("state_registration", 20)
//    val muninipalRegistration = varchar("municipal_registration", 20)
//    val address = uuid("address")
//}
//
//// *** PHONE ***
//
//class Phone(id: EntityID<UUID>) : UUIDEntity(id) {
//    companion object : UUIDEntityClass<Phone>(PhoneTable)
//
//    var internalId by PhoneTable.internalId
//    var phone by PhoneTable.phone
//    var idLegalPerson by LegalPerson referencedOn PhoneTable.idLegalPerson
//}
//
//object PhoneTable : UUIDTable("person.phone_legal_person") {
//    val internalId = long("internal_id").autoIncrement()
//    val phone = uuid("phone")
//    val idLegalPerson = reference("id_legal_person", LegalPersonTable)
//}