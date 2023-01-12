package com.cgi.bonnie.commplugin.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

public class OrderJson {
    private String shopOrderId;
    private String goodsId;
    private int quantity;
    //fixme  pattern has to be stored in .properties file, but
    //  spring.jackson.date-format: yyyy.MM.dd HH:mm
    //  is not working from there
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime placementDate;
    private JsonNode metadata;

    public OrderJson() {
    }

    @JsonCreator
    public OrderJson(@JsonProperty("shopOrderId") String shopOrderId,
                     @JsonProperty("goodsId") String goodsId,
                     @JsonProperty("quantity") int quantity,
                     @JsonProperty("placementDate") LocalDateTime placementDate,
                     @JsonProperty("metadata") JsonNode metadata) {
        this.shopOrderId = shopOrderId;
        this.goodsId = goodsId;
        this.quantity = quantity;
        this.placementDate = placementDate;
        this.metadata = metadata;
    }

    public String getShopOrderId() {
        return shopOrderId;
    }

    public OrderJson setShopOrderId(String shopOrderId) {
        this.shopOrderId = shopOrderId;
        return this;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public OrderJson setGoodsId(String goodsId) {
        this.goodsId = goodsId;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderJson setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public LocalDateTime getPlacementDate() {
        return placementDate;
    }

    public OrderJson setPlacementDate(LocalDateTime placementDate) {
        this.placementDate = placementDate;
        return this;
    }

    public JsonNode getMetadata() {
        return metadata;
    }

    @JsonIgnore
    public String getMetadataAsText() {
        return metadata.toString();
    }

    public OrderJson setMetadata(JsonNode details) {
        this.metadata = details;
        return this;
    }
}
