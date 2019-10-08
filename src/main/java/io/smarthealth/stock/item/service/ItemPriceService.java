/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.item.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.data.ItemPriceData;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemPrice;
import io.smarthealth.stock.item.domain.ItemPriceRepository;
import io.smarthealth.stock.item.domain.ItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
public class ItemPriceService {

    @Autowired
    ItemPriceRepository itemPriceRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ItemRepository itemRepository;

    @Transactional
    public ItemPrice createItemPrice(final ItemPriceData itemPriceData) {
        try {
            ItemPrice itemPrice = modelMapper.map(itemPriceData, ItemPrice.class);
            //fetch item 
            Item item = itemRepository.findById(itemPriceData.getItemId()).orElseThrow(() -> APIException.notFound("Item identified by {0} while creating item price was not found", itemPriceData.getItemId()));
            itemPrice.setItem(item);
            itemPriceRepository.save(itemPrice);
            return itemPrice;
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error creating item price", e.getMessage());
        }
    }

    public Page<ItemPrice> fetchItemPriceByItem(final Item item, final Pageable pageable) {
        return itemPriceRepository.findByItem(item, pageable);
    }

}
