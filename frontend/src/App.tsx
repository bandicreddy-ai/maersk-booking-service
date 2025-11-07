import React, { useState } from 'react'
import { http } from './api'
const Field = ({label, children}:{label:string, children:React.ReactNode}) => (<label style={{display:'block', margin:'8px 0'}}><div style={{fontSize:12, opacity:.8}}>{label}</div>{children}</label>)
export default function App() {
  const [apiKey, setApiKey] = useState(localStorage.getItem('X-API-KEY') || 'dev-secret')
  const [containerSize, setContainerSize] = useState(20)
  const [containerType, setContainerType] = useState('DRY')
  const [origin, setOrigin] = useState('Southampton')
  const [destination, setDestination] = useState('Singapore')
  const [quantity, setQuantity] = useState(5)
  const [timestamp, setTimestamp] = useState('2020-10-12T13:53:09Z')
  const [availability, setAvailability] = useState<null | boolean>(null)
  const [bookingRef, setBookingRef] = useState<string>('')
  const saveKey = () => { localStorage.setItem('X-API-KEY', apiKey); alert('API key saved') }
  const checkAvailability = async () => {
    setAvailability(null); setBookingRef('')
    const body = { containerSize, containerType, origin, destination, quantity }
    const res = await http.post('/api/bookings/availability', body)
    setAvailability(res.data.available)
  }
  const createBooking = async () => {
    setAvailability(null); setBookingRef('')
    const body = { containerSize, containerType, origin, destination, quantity, timestamp }
    const res = await http.post('/api/bookings', body)
    setBookingRef(res.data.bookingRef)
  }
  return (
    <div style={{maxWidth:720, margin:'24px auto', fontFamily:'Inter, system-ui, Arial'}}>
      <h2>Maersk Booking</h2>
      <div style={{padding:16, border:'1px solid #ddd', borderRadius:8, marginBottom:16}}>
        <Field label="Backend URL (env VITE_BACKEND_URL)"><code>{(import.meta as any).env.VITE_BACKEND_URL || 'http://localhost:8080'}</code></Field>
        <Field label="X-API-KEY"><input value={apiKey} onChange={e=>setApiKey(e.target.value)} /><button onClick={saveKey} style={{marginLeft:8}}>Save</button></Field>
      </div>
      <div style={{display:'grid', gridTemplateColumns:'1fr 1fr', gap:12}}>
        <Field label="Container Size"><select value={containerSize} onChange={e=>setContainerSize(parseInt(e.target.value))}><option value={20}>20</option><option value={40}>40</option></select></Field>
        <Field label="Container Type"><select value={containerType} onChange={e=>setContainerType(e.target.value)}><option>DRY</option><option>REEFER</option></select></Field>
        <Field label="Origin (5-20)"><input value={origin} onChange={e=>setOrigin(e.target.value)} /></Field>
        <Field label="Destination (5-20)"><input value={destination} onChange={e=>setDestination(e.target.value)} /></Field>
        <Field label="Quantity (1-100)"><input type="number" value={quantity} onChange={e=>setQuantity(parseInt(e.target.value||'0'))} /></Field>
        <Field label="Timestamp (UTC, for Create)"><input value={timestamp} onChange={e=>setTimestamp(e.target.value)} /></Field>
      </div>
      <div style={{marginTop:16}}>
        <button onClick={checkAvailability}>Check Availability</button>
        <button onClick={createBooking} style={{marginLeft:8}}>Create Booking</button>
      </div>
      <div style={{marginTop:16}}>
        {availability !== null && (<div>Available: <b>{availability ? 'true' : 'false'}</b></div>)}
        {bookingRef && (<div>bookingRef: <b>{bookingRef}</b></div>)}
      </div>
    </div>
  )
}
