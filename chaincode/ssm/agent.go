// Copyright Luc Yriarte <luc.yriarte@thingagora.org> 2018 
// License: Apache-2.0

package main

import (
	"errors"

	"crypto"
	"crypto/x509"
	"encoding/json"
	"encoding/pem"

	"github.com/hyperledger/fabric/core/chaincode/shim"
)

type Agent struct {
	AgentModel
}

//
// Storable interface implementation
//

func (self *Agent) Put(stub shim.ChaincodeStubInterface, key string) error {
	data, err := self.Serialize()
	if (err != nil) {
		return err
	}	
	return stub.PutState(key, data)
}

func (self *Agent) Get(stub shim.ChaincodeStubInterface, key string) error {
	data, err := stub.GetState(key);
	if (err != nil) {
		return err
	}	
	return self.Deserialize(data)
}

//
// Serializable interface implementation
//

func (self *Agent) Serialize() ([]byte, error) {
	return json.Marshal(self.AgentModel)
}

func (self *Agent) Deserialize(data []byte) error {
	err := json.Unmarshal(data, &self.AgentModel)
	if (err != nil) {
		return err
	}
	_, err = self.PublicKey()
	return err
}

//
// Agent API implementation
//

func (self *Agent) PublicKey() (crypto.PublicKey, error) {

	block, _ := pem.Decode([]byte(self.AgentModel.Pub))
	if block == nil {
		return nil, errors.New("Invalid agent PEM block")
	}
	if block.Type != "PUBLIC KEY" {
		return nil, errors.New("Agent PEM block is not a public key")
	}

	return x509.ParsePKIXPublicKey(block.Bytes)
}
