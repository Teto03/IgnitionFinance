package com.unimib.ignitionfinance.data.remote.response


import com.google.gson.annotations.SerializedName

data class ExchangeResponse(
    @SerializedName("header") val header: ExchangeHeader,
    @SerializedName("dataSets") val dataSets: List<ExchangeDataSet>,
    @SerializedName("structure") val structure: ExchangeStructure
)

data class ExchangeHeader(
    @SerializedName("id") val id: String,
    @SerializedName("test") val test: Boolean,
    @SerializedName("prepared") val prepared: String,
    @SerializedName("sender") val sender: ExchangeSender
)

data class ExchangeSender(
    @SerializedName("id") val id: String
)

data class ExchangeDataSet(
    @SerializedName("action") val action: String,
    @SerializedName("validFrom") val validFrom: String,
    @SerializedName("series") val series: Map<String, ExchangeSeries>
)

data class ExchangeSeries(
    @SerializedName("attributes") val attributes: List<Any?>,
    @SerializedName("observations") val observations: Map<String, List<Any?>>
)

data class ExchangeStructure(
    @SerializedName("links") val links: List<ExchangeLink>,
    @SerializedName("name") val name: String,
    @SerializedName("dimensions") val dimensions: ExchangeDimensions
)

data class ExchangeLink(
    @SerializedName("title") val title: String,
    @SerializedName("rel") val rel: String,
    @SerializedName("href") val href: String
)

data class ExchangeDimensions(
    @SerializedName("series") val series: List<ExchangeSeriesDimension>,
    @SerializedName("observation") val observation: List<ExchangeObservationDimension>
)

data class ExchangeSeriesDimension(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("values") val values: List<ExchangeValue>
)

data class ExchangeObservationDimension(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String,
    @SerializedName("values") val values: List<ExchangeValue>
)

data class ExchangeValue(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)
