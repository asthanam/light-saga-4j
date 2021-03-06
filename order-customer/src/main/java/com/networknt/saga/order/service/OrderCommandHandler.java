package com.networknt.saga.order.service;


import com.networknt.saga.order.domain.Order;
import com.networknt.saga.order.domain.OrderRepository;
import com.networknt.saga.order.saga.participants.ApproveOrderCommand;
import com.networknt.saga.order.saga.participants.RejectOrderCommand;
import com.networknt.saga.participant.SagaCommandHandlersBuilder;
import com.networknt.tram.command.consumer.CommandHandlers;
import com.networknt.tram.command.consumer.CommandMessage;
import com.networknt.tram.message.common.Message;

import static com.networknt.tram.command.consumer.CommandHandlerReplyBuilder.withSuccess;


public class OrderCommandHandler {

  private OrderRepository orderRepository;

  public OrderCommandHandler(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public CommandHandlers commandHandlerDefinitions() {
    return SagaCommandHandlersBuilder
            .fromChannel("orderService")
            .onMessage(ApproveOrderCommand.class, this::approve)
            .onMessage(RejectOrderCommand.class, this::reject)
            .build();
  }

  public Message approve(CommandMessage<ApproveOrderCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    Order order = orderRepository.findOne(orderId);
    order.noteCreditReserved();
    return withSuccess();
  }

  public Message reject(CommandMessage<RejectOrderCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    Order order = orderRepository.findOne(orderId);
    order.noteCreditReservationFailed();
    return withSuccess();
  }

}
