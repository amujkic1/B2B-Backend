package com.example.demo.repositories;

import com.example.demo.models.WebhookSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, Long> {
     WebhookSubscription findByUserId(Long userId);
}
