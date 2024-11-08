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
        this.transform =
            Transform to {
                Input("$[*].node") then 
                ListOf {
                    Object {
                        "body_style" from "$.vehicle.bodyType.value"
                        "description" call {
                            "interpolate"("{} ({})", "$.title.localized", "$.subtitle.localized")
                        }
                        "exterior_color" from "$.vehicle.exteriorColor.colorGroup.localized"
                        "interior_color" from "$.vehicle.interior.name.localized"
                        "image" call {
                            "mapImages"("eu", "$", "16-9", "de")
                        }
                        "make" to "Porsche"
                        "mileage" call {
                            "interpolate"("{} {}", "$.vehicle.mileage.value", "$.vehicle.mileage.unit")
                        }
                        "model" from "$.vehicle.modelSeries.localized"
                        "state_of_vehicle" call {
                            "branchOnEquals"(
                                "$.vehicle.condition.value",
                                "new",
                                "NEW",
                                Call {
                                    "branchOnEquals"("$.warranty.porscheApproved", true, "CPO", "USED")
                                }
                            )
                        }
                        "title" from "$.title.localized"
                        "url" call {
                            "interpolate"("https://finder.porsche.com/{}/{}/details/{}", "eu", "de", "$.id")
                        }
                        "vehicle_id" from "$.id"
                        "vin" from "$.vehicle.vin"
                        "year" from "$.vehicle.modelYear"
                        "condition" call {
                            "branchOnEquals"("$.vehicle.condition.value", "new", "EXCELLENT", "GOOD")
                        }
                        "drivetrain" call {
                            "branchOnEquals"(
                                "$.vehicle.drivetrain.value",
                                "ALL_WHEEL_DRIVE",
                                "AWD",
                                Call {
                                    "branchOnEquals"("$.vehicle.drivetrain.value", "REAR_WHEEL_DRIVE", "RWD", null)
                                }
                            )
                        }
                        "fuel_type" from "$.vehicle.engineType.value"
                        "transmission" call {
                            "branchOnEquals"("$.vehicle.transmission.value", "MANUAL", "MANUAL", "AUTOMATIC")
                        }
                        "trim" from "$.vehicle.modelCategory.localized"
                        "price" call {
                            "interpolate"("{} {}", "$.price.value", "$.price.currencyCode")
                        }
                        "latitude" from "$.location.latitude"
                        "longitude" from "$.location.longitude"
                        "address" {
                            "addr1" from "$.seller.addressComponents.localized.street"
                            "city" from "$.seller.addressComponents.localized.city"
                            "region" from "$.seller.addressComponents.localized.state"
                            "country" to "Deutschland"
                        }
                        "dealer_name" from "$.seller.name.localized"
                        "custom_label_0" from "$.vehicle.modelYear"
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