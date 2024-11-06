package com.digitalfrontiers.dataconnectiontool.controller

import com.digitalfrontiers.dataconnectiontool.service.ITransformationService
import com.digitalfrontiers.datatransformlang.transform.*
import com.digitalfrontiers.datatransformlang.transform.Specification
import com.digitalfrontiers.datatransformlang.transform.registerFunction
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

    private val spec: Specification

    init {

        registerFunction<List<Any>, String>(
            "interpolate",
            {args -> args.drop(1).fold(args[0] as String) { acc, arg -> acc.replaceFirst("{}", arg.toString()) }}
        )

        registerFunction<List<Any>, String>(
            "mapImages",
            {args ->
                val marketplaceId = args[0] as String;
                val listing = args[1] as Map<*, *>;
                val imageType = args[2] as String;
                val languageTag = args[3] as String;

                val id = listing["id"] as String;
                val updatedAt = listing["updatedAt"] as String;
                val updateTimestamp = Instant.parse(updatedAt).toEpochMilli()
                "https://social-media-images.slfinpub.aws.porsche.cloud/$marketplaceId/$id/$imageType/$languageTag?updated=$updateTimestamp"
            }
        )

        registerFunction<List<Any?>, Any?>(
            "branchOnEquals",
            {args ->
                if (args[0] == args[1])
                    args[2]
                else
                    args[3]
            }
        )

        val listing = "\$.node"
        this.spec =
        ForEach(
            ToObject {
                "body_style" from "$listing.vehicle.bodyType.value"
                "description" to Call(
                    "interpolate",
                    ToConst("{} ({})"),
                    ToInput("$listing.title.localized"),
                    ToInput("$listing.subtitle.localized")
                )
                "exterior_color" from "$listing.vehicle.exteriorColor.colorGroup.localized"
                "interior_color" from "$listing.vehicle.interior.name.localized"
                "image" to Call(
                    "mapImages",
                    ToConst("eu"),
                    ToInput(listing),
                    ToConst("16-9"),
                    ToConst("de")
                )
                "make" to "Porsche"
                "mileage" to Call(
                    "interpolate",
                    ToConst("{} {}"),
                    ToInput("$listing.vehicle.mileage.value"),
                    ToInput("$listing.vehicle.mileage.unit")
                )
                "model" from "$listing.vehicle.modelSeries.localized"
                "state_of_vehicle" to Call(
                    "branchOnEquals",
                    ToInput("$listing.vehicle.condition.value"),
                    ToConst("new"),
                    ToConst("NEW"),
                    Call(
                        "branchOnEquals",
                        ToInput("$listing.warranty.porscheApproved"),
                        ToConst(true),
                        ToConst("CPO"),
                        ToConst("USED")
                    )
                )
                "title" from "$listing.title.localized"
                "url" to Call(
                    "interpolate",
                    ToConst("https://finder.porsche.com/{}/{}/details/{}"),
                    ToConst("eu"),
                    ToConst("de"),
                    ToInput("$listing.id")
                )
                "vehicle_id" from "$listing.id"
                "vin" from "$listing.vehicle.vin"
                "year" from "$listing.vehicle.modelYear"
                "condition" to Call(
                    "branchOnEquals",
                    ToInput("$listing.vehicle.condition.value"),
                    ToConst("new"),
                    ToConst("EXCELLENT"),
                    ToConst("GOOD")
                )
                "drivetrain" to Call(
                    "branchOnEquals",
                    ToInput("$listing.vehicle.drivetrain.value"),
                    ToConst("ALL_WHEEL_DRIVE"),
                    ToConst("AWD"),
                    Call(
                        "branchOnEquals",
                        ToInput("$listing.vehicle.drivetrain.value"),
                        ToConst("REAR_WHEEL_DRIVE"),
                        ToConst("RWD"),
                        ToConst(null)
                    )
                )
                "fuel_type" from "$listing.vehicle.engineType.value"
                "transmission" to Call(
                    "branchOnEquals",
                    ToInput("$listing.vehicle.transmission.value"),
                    ToConst("MANUAL"),
                    ToConst("MANUAL"),
                    ToConst("AUTOMATIC")
                )
                "trim" from "$listing.vehicle.modelCategory.localized"
                "price" to Call(
                    "interpolate",
                    ToConst("{} {}"),
                    ToInput("$listing.price.value"),
                    ToInput("$listing.price.currencyCode")
                )
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
        )
    }

    @PostMapping("/meta-auto")
    fun process(@RequestBody body: String): String {
        return this.transformer.transform(body, this.spec)
    }
}