package com.grid07.assignment.controller;

import com.grid07.assignment.entity.Bot;
import com.grid07.assignment.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bots")
public class BotController {

    // Not using constructor injection for now, but maybe later
    @Autowired
    private BotRepository botRepo;

    // CREATE a new Bot
    @PostMapping
    public ResponseEntity<Bot> addBot(@RequestBody Bot newBot) {
        // Could add input validation here?
        Bot b = botRepo.save(newBot);
        // Not handling errors yet, just returning whatever is saved
        return ResponseEntity.ok(b);
    }

    // Get all bots - maybe add pagination at some point
    @GetMapping
    public List<Bot> listBots() {
        // Just fetch everything for now
        List<Bot> bots = botRepo.findAll();
        return bots;
    }

    // Get one bot by its id
    @GetMapping("/{id}")
    public ResponseEntity<Bot> fetchBot(@PathVariable("id") Long botId) {
        // Not super happy with this, maybe refactor later
        Bot found = null;
        // could use Optional, but let's go step by step
        if (botRepo.findById(botId).isPresent()) {
            found = botRepo.findById(botId).get();
            return ResponseEntity.ok(found);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update the bot
    @PutMapping("/{id}")
    public ResponseEntity<Bot> editBot(@PathVariable("id") Long id, @RequestBody Bot incomingBot) {
        // Small note: Might be slower to call findById twice, but it's fine for now
        Bot botToUpdate = null;
        if (botRepo.findById(id).isPresent()) {
            botToUpdate = botRepo.findById(id).get();
            // Only updating name and persona for now
            botToUpdate.setName(incomingBot.getName());
            botToUpdate.setPersonaDescription(incomingBot.getPersonaDescription());
            Bot afterSave = botRepo.save(botToUpdate);
            return ResponseEntity.ok(afterSave);
        } else {
            // Not found, nothing to update
            return ResponseEntity.notFound().build();
        }
    }

    // Remove a bot from the system
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeBot(@PathVariable("id") Long botId) {
        // Just using findById to check existence
        if (botRepo.findById(botId).isPresent()) {
            Bot bot = botRepo.findById(botId).get();
            botRepo.delete(bot);
            return ResponseEntity.noContent().<Void>build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}