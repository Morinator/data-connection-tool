package com.digitalfrontiers.dataconnectiontool.controller

import com.digitalfrontiers.dataconnectiontool.service.ITransformationService
import com.digitalfrontiers.datatransformlang.Transform
import com.digitalfrontiers.datatransformlang.transform.*
import com.digitalfrontiers.datatransformlang.transform.Specification
import com.digitalfrontiers.datatransformlang.transform.convert.defaults.CSVSerializer
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
            ToObject(
                "body_style" to Fetch("$listing.vehicle.bodyType.value"),
                "description" to Call(
                    "interpolate",
                    Const("{} ({})"),
                    Fetch("$listing.title.localized"),
                    Fetch("$listing.subtitle.localized")
                ),
                "exterior_color" to Fetch("$listing.vehicle.exteriorColor.colorGroup.localized"),
                "interior_color" to Fetch("$listing.vehicle.interior.name.localized"),
                "image" to Call(
                    "mapImages",
                    Const("eu"),
                    Fetch(listing),
                    Const("16-9"),
                    Const("de")
                ),
                "make" to Const("Porsche"),
                "mileage" to Call(
                    "interpolate",
                    Const("{} {}"),
                    Fetch("$listing.vehicle.mileage.value"),
                    Fetch("$listing.vehicle.mileage.unit")
                ),
                "model" to Fetch("$listing.vehicle.modelSeries.localized"),
                "state_of_vehicle" to Call(
                    "branchOnEquals",
                    Fetch("$listing.vehicle.condition.value"),
                    Const("new"),
                    Const("NEW"),
                    Call(
                        "branchOnEquals",
                        Fetch("$listing.warranty.porscheApproved"),
                        Const(true),
                        Const("CPO"),
                        Const("USED")
                    )
                ),
                "title" to Fetch("$listing.title.localized"),
                "url" to Call(
                    "interpolate",
                    Const("https://finder.porsche.com/{}/{}/details/{}"),
                    Const("eu"),
                    Const("de"),
                    Fetch("$listing.id")
                ),
                "vehicle_id" to Fetch("$listing.id"),
                "vin" to Fetch("$listing.vehicle.vin"),
                "year" to Fetch("$listing.vehicle.modelYear"),
                "condition" to Call(
                    "branchOnEquals",
                    Fetch("$listing.vehicle.condition.value"),
                    Const("new"),
                    Const("EXCELLENT"),
                    Const("GOOD")
                ),
                "drivetrain" to Call(
                    "branchOnEquals",
                    Fetch("$listing.vehicle.drivetrain.value"),
                    Const("ALL_WHEEL_DRIVE"),
                    Const("AWD"),
                    Call(
                        "branchOnEquals",
                        Fetch("$listing.vehicle.drivetrain.value"),
                        Const("REAR_WHEEL_DRIVE"),
                        Const("RWD"),
                        Const(null)
                    )
                ),
                "fuel_type" to Fetch("$listing.vehicle.engineType.value"),
                "transmission" to Call(
                    "branchOnEquals",
                    Fetch("$listing.vehicle.transmission.value"),
                    Const("MANUAL"),
                    Const("MANUAL"),
                    Const("AUTOMATIC")
                ),
                "trim" to Fetch("$listing.vehicle.modelCategory.localized"),
                "price" to Call(
                    "interpolate",
                    Const("{} {}"),
                    Fetch("$listing.price.value"),
                    Fetch("$listing.price.currencyCode")
                ),
                "latitude" to Fetch("$listing.location.latitude"),
                "longitude" to Fetch("$listing.location.longitude"),
                "address" to ToObject(
                    "addr1" to Fetch("$listing.seller.addressComponents.localized.street"),
                    "city" to Fetch("$listing.seller.addressComponents.localized.city"),
                    "region" to Fetch("$listing.seller.addressComponents.localized.state"),
                    "country" to Const("Deutschland")
                ),
                "dealer_name" to Fetch("$listing.seller.name.localized"),
                "custom_label_0" to Fetch("$listing.vehicle.modelYear")
            )
        )
    }

    @PostMapping("/meta-auto")
    fun process(@RequestBody body: String): String {
        return this.transformer.transform(body, this.spec)
    }
}