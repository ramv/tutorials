from card import Card

class Player(object):

    def __init__(self, bet):
        self.current_bet = bet
        self.cards =[]
        #default number of coins that each player has
        self.wallet  = 100
        self.wallet -= bet
    
    def clear_hand(self):
        del self.cards
        self.cards =[]
        
    def set_bet(self, bet):
        if bet > self.wallet :
            raise ValueError, 'cannot bet more than the number of coins in the wallet. Wallet: '+str(self.wallet)+ 'Bet: '+str(bet)
        self.wallet -= bet
        self.current_bet = bet
        
    def get_bet(self):
        return self.current_bet
    
    def win(self, coins):
        #print "########## Adding coins: "+str(coins)+" to wallet: "+ str(self.wallet)
        self.wallet += coins
        
    def add_to_wallet(self,coins):
        self.wallet += coins
        
    def get_wallet(self):
        return self.wallet
        
    def push_card(self, card):
        if type(card) == Card: 
            self.cards.append(card)
        else:
            raise InvalidArgumentError, "The argument is not of the type card"
            
    def get_card(self, idx):
        if idx < len(self.cards):
            return str(self.cards[idx])
        else:
            raise ValueError, "no card at index: "+str(idx)+" number of cards: "+str(len(self.cards))
    
    def __str__(self):
        res = []
        for card in self.cards:
            res.append(str(card))
        return 'CARDS: '+', '.join(res) +'; BET: '+str(self.current_bet)+'; WALLET: '+str(self.wallet)
             
    def show_cards(self):
        res = []
        for card in self.cards:
            res.append(str(card))
        return ', '.join(res)

    def get_sum(self):
        sum = 0 
        num_aces = 0
        for card in self.cards :
            rank = card.get_rank()  
            if rank == 1 :
                num_aces += 1
                sum += 11
            elif rank <=10:
                sum += rank
            elif rank > 10:
                sum += 10
            else:
                # should not occur
                raise ValueError, "Don't know how to handle rank: "+str(rank)
        for num in range(num_aces) :
            if sum > 21:
                sum -= 10
            
        return sum
            
        