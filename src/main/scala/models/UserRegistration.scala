package models

case class UserRegistration(
                             id: Int,
                             step: Option[RegistrationStep.Value],
                             complete: Boolean,
                             firstName: Option[String],
                             lastName:Option[String],
                             phone: Option[String]
                           )




