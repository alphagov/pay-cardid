package uk.gov.pay.card.cloudfront;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.jce.JceMasterKey;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import uk.gov.pay.card.model.CardInformationRequest;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class EncryptedCardInformationRequestDeserializer extends StdDeserializer<CardInformationRequest> {
    private final PrivateKey privateKey;
    private final String keyName;
    private final String keyProvider;

    public EncryptedCardInformationRequestDeserializer(CloudfrontServiceBinding cloudfront) throws InvalidKeySpecException, NoSuchAlgorithmException {
        this(cloudfront, null);
    }

    protected EncryptedCardInformationRequestDeserializer(CloudfrontServiceBinding cloudfront, Class<?> vc) throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(vc);
        byte[] key = Base64.getDecoder().decode(cloudfront.getPrivateKey());
        this.keyName = cloudfront.getKeyName();
        this.keyProvider = cloudfront.getKeyProvider();
        this.privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(key));
    }

    @Override
    public CardInformationRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String encryptedCardNumber = node.get("cardNumber").textValue();
        String cardNumber = decrypt(encryptedCardNumber);
        return new CardInformationRequest(cardNumber);
    }
    
    private String decrypt(final String encryptedEncodedString) {
        final byte[] encryptedBytes = Base64.getDecoder().decode(encryptedEncodedString);
        final AwsCrypto crypto = new AwsCrypto();
        final JceMasterKey masterKey = JceMasterKey.getInstance(
                null,
                privateKey,
                keyProvider,
                keyName,
                "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
        );
        final CryptoResult<byte[], ?> result = crypto.decryptData(masterKey, encryptedBytes);
        return new String(result.getResult());
    }
}
