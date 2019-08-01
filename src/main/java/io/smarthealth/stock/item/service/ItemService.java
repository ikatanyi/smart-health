/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.item.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;
import io.smarthealth.stock.item.domain.Uom;
import io.smarthealth.stock.item.domain.UomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class ItemService {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UomRepository uomRepository;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public Item createItem(ItemData itemData) {
        try {
            //look up barcode, if exists, throw exists error
            if (itemRepository.existsByBarcode(itemData.getBarcode())) {
                throw APIException.conflict("Duplicate Item Barcode {0}", itemData.getBarcode());
            }
            Item item = modelMapper.map(itemData, Item.class);
            Uom uom = uomRepository.findById(itemData.getUomId()).orElseThrow(() -> APIException.notFound("", ""));
            item.setUom(uom);

            item = itemRepository.saveAndFlush(item);

            return item;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error creating item object ", e.getMessage());
        }
    }

    public Item fetchItemById(final Long itemId) {
        try {
            return itemRepository.findById(itemId).orElseThrow(() -> APIException.notFound("Item identified by {0} not found", itemId));
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error fetching item id " + itemId, e.getMessage());
        }
    }

    public Item fetchItemByCode(final String itemCode) {
        try {
            return itemRepository.findByItemCode(itemCode).orElseThrow(() -> APIException.notFound("Item identified by code {0} not found", itemCode));
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error fetching item code " + itemCode, e.getMessage());
        }
    }

    @Transactional
    public Item updateItem(final Long itemId, ItemData itemData) {
        try {
            Item itemExisting = itemRepository.findById(itemId).orElseThrow(() -> APIException.notFound("Item identified by {0} not found", itemId));
            modelMapper.map(itemData, itemExisting);
            Uom uom = uomRepository.findById(itemData.getUomId()).orElseThrow(() -> APIException.notFound("", ""));
            itemExisting.setUom(uom);
            itemRepository.saveAndFlush(itemExisting);
            return itemExisting;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error updating item id " + itemId, e.getMessage());
        }
    }

    public Page<Item> fetchAllItems(final Pageable pageable) {
        try {

            return itemRepository.findAll(pageable);

        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error fetching all items ", e.getMessage());
        }
    }
}
