package com.digitalfrontiers.dataconnectiontool.controller

import com.digitalfrontiers.dataconnectiontool.service.ITransformationService
import com.digitalfrontiers.datatransformlang.Transform
import com.digitalfrontiers.datatransformlang.transform.*
import com.digitalfrontiers.datatransformlang.with
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/connectors", consumes = [MediaType.APPLICATION_JSON_VALUE])
class MetaAutomotiveController(
    @Autowired private val transformer: ITransformationService<String, String>
) {
    private final val transform: Transform

    init {
        val listing = "\$.node"
        this.transform =
            Transform to {
                ListOf {
                    Object {
                        "body_style" from "$listing.vehicle.bodyType.value"
                        "description" call {
                            "interpolate"("{} ({})", "$listing.title.localized", "$listing.subtitle.localized")
                        }
                        "exterior_color" from "$listing.vehicle.exteriorColor.colorGroup.localized"
                        "interior_color" from "$listing.vehicle.interior.name.localized"
                        "image" call {
                            "mapImages"("eu", listing, "16-9", "de")
                        }
                        "make" to "Porsche"
                        "mileage" call {
                            "interpolate"("{} {}", "$listing.vehicle.mileage.value", "$listing.vehicle.mileage.unit")
                        }
                        "model" from "$listing.vehicle.modelSeries.localized"
                        "state_of_vehicle" call {
                            "branchOnEquals"(
                                "$listing.vehicle.condition.value",
                                "new",
                                "NEW",
                                Call {
                                    "branchOnEquals"("$listing.warranty.porscheApproved", true, "CPO", "USED")
                                }
                            )
                        }
                        "title" from "$listing.title.localized"
                        "url" call {
                            "interpolate"("https://finder.porsche.com/{}/{}/details/{}", "eu", "de", "$listing.id")
                        }
                        "vehicle_id" from "$listing.id"
                        "vin" from "$listing.vehicle.vin"
                        "year" from "$listing.vehicle.modelYear"
                        "condition" call {
                            "branchOnEquals"("$listing.vehicle.condition.value", "new", "EXCELLENT", "GOOD")
                        }
                        "drivetrain" call {
                            "branchOnEquals"(
                                "$listing.vehicle.drivetrain.value",
                                "ALL_WHEEL_DRIVE",
                                "AWD",
                                Call {
                                    "branchOnEquals"("$listing.vehicle.drivetrain.value", "REAR_WHEEL_DRIVE", "RWD", null)
                                }
                            )
                        }
                        "fuel_type" from "$listing.vehicle.engineType.value"
                        "transmission" call {
                            "branchOnEquals"("$listing.vehicle.transmission.value", "MANUAL", "MANUAL", "AUTOMATIC")
                        }
                        "trim" from "$listing.vehicle.modelCategory.localized"
                        "price" call {
                            "interpolate"("{} {}", "$listing.price.value", "$listing.price.currencyCode")
                        }
                        "latitude" from "$listing.location.latitude"
                        "longitude" from "$listing.location.longitude"
                        "address" {
                            "addr1" from "$listing.seller.addressComponents.localized.street"
                            "city" from "$listing.seller.addressComponents.localized.city"
                            "region" from "$listing.seller.addressComponents.localized.state"
                            "country" to "Deutschland"
                        }
                        "dealer_name" from "$listing.seller.name.localized"
                        "custom_label_0" from "$listing.vehicle.modelYear"
                    }
                }
            } with {
                function("interpolate") {
                    args ->
                    args.drop(1).fold(args[0] as String) { acc, arg -> acc.replaceFirst("{}", arg.toString()) }
                }

                function("mapImages") {
                    args ->
                    val marketplaceId = args[0] as String;
                    val listing = args[1] as Map<*, *>;
                    val imageType = args[2] as String;
                    val languageTag = args[3] as String;

                    val id = listing["id"] as String;
                    val updatedAt = listing["updatedAt"] as String;
                    val updateTimestamp = Instant.parse(updatedAt).toEpochMilli()
                    "https://social-media-images.slfinpub.aws.porsche.cloud/$marketplaceId/$id/$imageType/$languageTag?updated=$updateTimestamp"
                }

                function("branchOnEquals") {
                    args ->
                    if (args[0] == args[1])
                        args[2]
                    else
                        args[3]
                }
            }
    }

    @PostMapping("/meta-auto")
    fun process(@RequestBody body: String): String {
        return this.transform.apply(body)
    }
}