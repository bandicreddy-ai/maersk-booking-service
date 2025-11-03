import React, { useState } from 'react'
import axios from 'axios'

export default function App(){
  const [availability, setAvailability] = useState(null)
  const [bookingRef, setBookingRef] = useState(null)
  const [payload, setPayload] = useState({
    containerType: 'DRY',
    containerSize: 20,
    origin: 'Chennai',
    destination: 'Singapore',
    quantity: 5,
    timestamp: new Date().toISOString()
  })

  const baseUrl = import.meta.env.VITE_API_BASE || 'http://localhost:8080'
  const headers = { 'X-API-KEY': (import.meta.env.VITE_API_KEY || 'dev-key') }

  const check = async () => {
    const res = await axios.post(`${baseUrl}/api/bookings/availability`, payload, { headers })
    setAvailability(res.data.available)
  }

  const book = async () => {
    const res = await axios.post(`${baseUrl}/api/bookings`, payload, { headers })
    setBookingRef(res.data.bookingRef)
  }

  return (
    <div style={{fontFamily:'sans-serif', padding:20}}>
      <h2>Maersk Booking UI</h2>
      <div style={{display:'grid', gridTemplateColumns:'1fr 1fr', gap:12, maxWidth:600}}>
        {['containerType','containerSize','origin','destination','quantity','timestamp'].map(k => (
          <label key={k} style={{display:'flex', flexDirection:'column'}}>
            {k}
            <input value={payload[k]} onChange={e=>setPayload({...payload,[k]: e.target.value})}/>
          </label>
        ))}
      </div>
      <div style={{marginTop:16}}>
        <button onClick={check}>Check Availability</button>
        <button onClick={book} style={{marginLeft:8}}>Create Booking</button>
      </div>
      {availability !== null && <p>Available: {String(availability)}</p>}
      {bookingRef && <p>Booking Ref: {bookingRef}</p>}
      <p>Swagger: <a href={`${baseUrl}/swagger-ui/index.html`} target="_blank">/swagger-ui</a></p>
    </div>
  )
}
