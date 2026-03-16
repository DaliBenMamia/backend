package coifure.coif.aiservice.application;

import coifure.coif.aiservice.web.dto.ChatRequest;
import coifure.coif.aiservice.web.dto.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ChatbotService {

    public ChatResponse reply(ChatRequest request) {
        String message = request.message().trim();
        String normalized = message.toLowerCase(Locale.ROOT);

        if (containsAny(normalized, "pas cher", "abordable", "prix")) {
            return new ChatResponse("Je vous recommande de filtrer les salons avec promotions actives et base tarifaire moderee, par exemple Coif Premium Tunis et Studio Capillaire Menzah.");
        }
        if (containsAny(normalized, "proche", "pres de moi", "à tunis", "a tunis", "tunis")) {
            return new ChatResponse("Pour Tunis, vous pouvez commencer par Coif Premium Tunis, Curl House Tunis et Beauty Corner Lac 2. Je peux ensuite affiner selon votre budget ou le service souhaite.");
        }
        if (containsAny(normalized, "ouverts", "ouvert", "aujourd", "today")) {
            return new ChatResponse("Les salons les plus susceptibles d'etre ouverts aujourd'hui sont ceux avec creneaux continus entre 09:00 et 19:00. Cote MVP, connectez cet endpoint plus tard a salon-service pour verifier les horaires en temps reel.");
        }
        if (containsAny(normalized, "service", "conseille", "recommande", "cheveux boucles", "boucles")) {
            return new ChatResponse("Pour des cheveux boucles, je conseille un soin nutritif defini-boucles suivi d'un coiffage protecteur. Les salons specialises peuvent etre priorises dans vos recommandations.");
        }
        if (containsAny(normalized, "reserver", "reservation", "demain", "apres-midi")) {
            return new ChatResponse("Vous pouvez reserver demain apres-midi sur un creneau 14:00-17:00. Dans une integration complete, ce chatbot appellera reservation-service pour proposer directement des disponibilites.");
        }
        if (containsAny(normalized, "promotion", "promotions", "reduction", "offre")) {
            return new ChatResponse("Les promotions les plus utiles concernent souvent le brushing express, les packs coupe + barbe et certains soins premium en debut de semaine.");
        }
        if (containsAny(normalized, "payer", "paiement", "carte", "en ligne")) {
            return new ChatResponse("Oui, le paiement en ligne peut etre active pour une reservation. Le MVP accepte deja les methodes CARD, CASH et ONLINE avec un statut de paiement extensible.");
        }
        if (containsAny(normalized, "profil", "photo")) {
            return new ChatResponse("Vous pourrez mettre a jour votre profil, votre telephone et votre photo depuis user-service avec un upload multipart.");
        }
        if (containsAny(normalized, "historique", "reservations", "mes reservations")) {
            return new ChatResponse("Votre historique doit etre expose par reservation-service via des endpoints par utilisateur et par coiffeur, avec les statuts et informations de paiement.");
        }

        return new ChatResponse("Je peux vous aider pour les salons, services, promotions, disponibilites, paiement, profil et historique. Posez-moi une question plus precise et je vous repondrai en francais.");
    }

    private boolean containsAny(String text, String... candidates) {
        for (String candidate : candidates) {
            if (text.contains(candidate)) {
                return true;
            }
        }
        return false;
    }
}
